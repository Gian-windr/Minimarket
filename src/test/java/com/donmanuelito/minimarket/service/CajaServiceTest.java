package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.dto.AperturaCajaRequest;
import com.donmanuelito.minimarket.dto.CierreCajaRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Caja;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.MetodoPago;
import com.donmanuelito.minimarket.repository.CajaRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CajaServiceTest {

    @Mock private CajaRepository cajaRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private AuthFacade authFacade;

    @InjectMocks
    private CajaService cajaService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = DatosPrueba.empleado(1);
    }

    @Test
    @DisplayName("El arqueo compara el efectivo contado contra el esperado por el sistema")
    void cerrarCalculaDiferenciaDeArqueo() {
        Caja caja = new Caja();
        caja.setIdCaja(7);
        caja.setEmpleado(empleado);
        caja.setMontoInicial(new BigDecimal("100.00"));

        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.of(caja));
        when(ventaRepository.sumTotalByCajaAndMetodoPago(7, MetodoPago.EFECTIVO, EstadoVenta.COMPLETADA))
                .thenReturn(new BigDecimal("250.00"));
        when(cajaRepository.save(any(Caja.class))).thenAnswer(i -> i.getArgument(0));

        Caja cerrada = cajaService.cerrar(new CierreCajaRequest(new BigDecimal("340.00")));

        // Esperado: 100.00 inicial + 250.00 en efectivo = 350.00; contado 340.00 -> faltan 10.00
        assertThat(cerrada.getMontoSistema()).isEqualByComparingTo("350.00");
        assertThat(cerrada.getDiferencia()).isEqualByComparingTo("-10.00");
        assertThat(cerrada.getEstadoCaja()).isEqualTo(EstadoCaja.CERRADA);
        assertThat(cerrada.getFechaCierre()).isNotNull();
    }

    @Test
    @DisplayName("Un cajero no puede abrir una segunda caja teniendo una abierta")
    void abrirConCajaAbiertaFalla() {
        Caja abierta = new Caja();
        abierta.setIdCaja(3);
        abierta.setEmpleado(empleado);

        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.of(abierta));

        assertThatThrownBy(() -> cajaService.abrir(new AperturaCajaRequest(new BigDecimal("50.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya tiene una caja abierta");

        verify(cajaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cerrar sin caja abierta da un error claro")
    void cerrarSinCajaAbiertaFalla() {
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cajaService.cerrar(new CierreCajaRequest(new BigDecimal("10.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No tiene una caja abierta");
    }
}
