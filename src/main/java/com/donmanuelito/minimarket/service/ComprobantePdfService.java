package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Comprobante;
import com.donmanuelito.minimarket.model.DetalleVenta;
import com.donmanuelito.minimarket.model.Venta;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/** Genera el PDF del comprobante en formato ticket de 80 mm. */
@Service
public class ComprobantePdfService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final float ANCHO_TICKET = 226f; // 80 mm en puntos

    private final Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private final Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 7);
    private final Font fontNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);

    @Value("${app.empresa.razon-social}")
    private String razonSocialEmpresa;

    @Value("${app.empresa.direccion}")
    private String direccionEmpresa;

    @Value("${app.comprobantes.ruta}")
    private String rutaComprobantes;

    public byte[] generar(Comprobante comprobante) {
        Venta venta = comprobante.getVenta();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        // Alto generoso: el ticket se recorta al contenido al imprimir
        Document documento = new Document(new Rectangle(ANCHO_TICKET, 700f), 12, 12, 12, 12);
        PdfWriter.getInstance(documento, salida);
        documento.open();

        agregarCentrado(documento, razonSocialEmpresa, fontTitulo);
        agregarCentrado(documento, "RUC: " + comprobante.getRucEmisor(), fontNormal);
        agregarCentrado(documento, direccionEmpresa, fontNormal);
        agregarCentrado(documento, " ", fontNormal);
        agregarCentrado(documento, comprobante.getTipoComprobante().name() + " ELECTRONICA", fontNegrita);
        agregarCentrado(documento, comprobante.getSerieComprobante() + "-" + comprobante.getNumeroComprobante(),
                fontNegrita);
        agregarSeparador(documento);

        agregarLinea(documento, "Fecha: " + comprobante.getFechaEmisionComprobante().format(FECHA));
        agregarLinea(documento, "Cajero: " + nombreEmpleado(venta));
        agregarLinea(documento, "Pago: " + venta.getMetodoPago().name());
        if (comprobante.getNumDocumento() != null) {
            agregarLinea(documento, "Doc. cliente: " + comprobante.getNumDocumento());
        }
        String cliente = comprobante.getRazonSocial() != null
                ? comprobante.getRazonSocial()
                : comprobante.getNombreCliente();
        if (cliente != null) {
            agregarLinea(documento, "Cliente: " + cliente);
        }
        if (comprobante.getDireccionFiscal() != null) {
            agregarLinea(documento, "Direccion: " + comprobante.getDireccionFiscal());
        }
        agregarSeparador(documento);

        documento.add(tablaDetalles(venta));
        agregarSeparador(documento);

        documento.add(tablaTotales(venta));
        agregarSeparador(documento);

        agregarCentrado(documento, "Gracias por su compra", fontNegrita);
        agregarCentrado(documento, "Representacion impresa del comprobante electronico", fontNormal);

        documento.close();
        return salida.toByteArray();
    }

    /** Escribe el PDF en disco y devuelve la ruta absoluta del archivo. */
    public String guardarEnDisco(Comprobante comprobante, byte[] pdf) {
        String nombre = comprobante.getSerieComprobante() + "-" + comprobante.getNumeroComprobante() + ".pdf";
        try {
            Path carpeta = Path.of(rutaComprobantes);
            Files.createDirectories(carpeta);
            Path archivo = carpeta.resolve(nombre);
            Files.write(archivo, pdf);
            return archivo.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el PDF del comprobante: " + e.getMessage());
        }
    }

    private PdfPTable tablaDetalles(Venta venta) {
        PdfPTable tabla = new PdfPTable(new float[]{3f, 1f, 1.6f, 1.8f});
        tabla.setWidthPercentage(100);

        encabezado(tabla, "Descripcion");
        encabezado(tabla, "Cant");
        encabezado(tabla, "P.Unit");
        encabezado(tabla, "Importe");

        for (DetalleVenta detalle : venta.getDetalles()) {
            celda(tabla, detalle.getProducto().getNombreProducto(), Element.ALIGN_LEFT);
            celda(tabla, String.valueOf(detalle.getCantidadDetalle()), Element.ALIGN_CENTER);
            celda(tabla, monto(detalle.getPrecioUnitarioDetalle()), Element.ALIGN_RIGHT);
            celda(tabla, monto(detalle.getSubtotalDetalle()), Element.ALIGN_RIGHT);

            if (detalle.getDescuentoAplicado() != null
                    && detalle.getDescuentoAplicado().compareTo(BigDecimal.ZERO) > 0) {
                celda(tabla, "  Descuento", Element.ALIGN_LEFT);
                celda(tabla, "", Element.ALIGN_CENTER);
                celda(tabla, "", Element.ALIGN_RIGHT);
                celda(tabla, "-" + monto(detalle.getDescuentoAplicado()), Element.ALIGN_RIGHT);
            }
        }
        return tabla;
    }

    private PdfPTable tablaTotales(Venta venta) {
        PdfPTable tabla = new PdfPTable(new float[]{2.5f, 1.5f});
        tabla.setWidthPercentage(100);

        filaTotal(tabla, "Op. Gravada:", monto(venta.getSubtotalVenta()), false);
        filaTotal(tabla, "IGV (18%):", monto(venta.getIgvVenta()), false);
        filaTotal(tabla, "TOTAL:", monto(venta.getTotalVenta()), true);
        return tabla;
    }

    private void filaTotal(PdfPTable tabla, String etiqueta, String valor, boolean destacado) {
        Font font = destacado ? fontNegrita : fontNormal;
        PdfPCell izquierda = new PdfPCell(new Phrase(etiqueta, font));
        izquierda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        izquierda.setBorder(Rectangle.NO_BORDER);
        PdfPCell derecha = new PdfPCell(new Phrase(valor, font));
        derecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
        derecha.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(izquierda);
        tabla.addCell(derecha);
    }

    private void encabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontNegrita));
        celda.setBorder(Rectangle.BOTTOM);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private void celda(PdfPTable tabla, String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontNormal));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(alineacion);
        tabla.addCell(celda);
    }

    private void agregarCentrado(Document documento, String texto, Font font) {
        Paragraph parrafo = new Paragraph(texto, font);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        documento.add(parrafo);
    }

    private void agregarLinea(Document documento, String texto) {
        documento.add(new Paragraph(texto, fontNormal));
    }

    private void agregarSeparador(Document documento) {
        Paragraph separador = new Paragraph("------------------------------------------------", fontNormal);
        separador.setAlignment(Element.ALIGN_CENTER);
        documento.add(separador);
    }

    private String monto(BigDecimal valor) {
        return valor == null ? "0.00" : String.format("%.2f", valor);
    }

    private String nombreEmpleado(Venta venta) {
        return venta.getEmpleado().getNombreEmpleado() + " " + venta.getEmpleado().getApellidoEmpleado();
    }
}
