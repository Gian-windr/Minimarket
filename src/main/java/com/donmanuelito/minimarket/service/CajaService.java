package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.AperturaCajaRequest;
import com.donmanuelito.minimarket.dto.CierreCajaRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Caja;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.MetodoPago;
import com.donmanuelito.minimarket.repository.CajaRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CajaService {

    private final CajaRepository cajaRepository;
    private final VentaRepository ventaRepository;
    private final AuthFacade authFacade;

    public List<Caja> listar() {
        return cajaRepository.findAll();
    }

    public Caja obtener(Integer id) {
        return cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", id));
    }

    /** Caja abierta del empleado autenticado. */
    public Caja actual() {
        Empleado empleado = authFacade.empleadoActual();
        return cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(
                        empleado.getIdEmpleado(), EstadoCaja.ABIERTA)
                .orElseThrow(() -> new BusinessException("No tiene una caja abierta"));
    }

    @Transactional
    public Caja abrir(AperturaCajaRequest request) {
        Empleado empleado = authFacade.empleadoActual();
        cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(
                empleado.getIdEmpleado(), EstadoCaja.ABIERTA).ifPresent(c -> {
            throw new BusinessException("Ya tiene una caja abierta (#" + c.getIdCaja() + ")");
        });

        Caja caja = new Caja();
        caja.setEmpleado(empleado);
        caja.setMontoInicial(request.montoInicial());
        return cajaRepository.save(caja);
    }

    /** Cierra la caja del empleado autenticado y calcula el arqueo. */
    @Transactional
    public Caja cerrar(CierreCajaRequest request) {
        Caja caja = actual();

        BigDecimal ventasEfectivo = ventaRepository.sumTotalByCajaAndMetodoPago(
                caja.getIdCaja(), MetodoPago.EFECTIVO, EstadoVenta.COMPLETADA);

        caja.setMontoSistema(caja.getMontoInicial().add(ventasEfectivo));
        caja.setMontoReal(request.montoReal());
        caja.setDiferencia(request.montoReal().subtract(caja.getMontoSistema()));
        caja.setFechaCierre(LocalDateTime.now());
        caja.setEstadoCaja(EstadoCaja.CERRADA);
        return cajaRepository.save(caja);
    }
}
