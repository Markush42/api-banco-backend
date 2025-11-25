package com.informaticonfig.spring.myapp1.springboot_applications.test;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.informaticonfig.spring.myapp1.springboot_applications.controllers.EmpleadoController;
import com.informaticonfig.spring.myapp1.springboot_applications.models.Empleado;
import com.informaticonfig.spring.myapp1.springboot_applications.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmpleadoController.class)
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void listarEmpleados_sinEmpleados_noRevientaYDevuelveVista() throws Exception {
        when(empleadoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/empleados/detalles"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalles_empleados"))
                .andExpect(model().attribute("EmpleadosComercio", hasSize(0)))
                .andExpect(model().attribute("UltimoEmpleado", nullValue()))
                .andExpect(model().attribute("Titulo", "Detalle de empleados"));
    }

    @Test
    void listarEmpleados_conEmpleados_exponeListaYUltimoEmpleado() throws Exception {
        Empleado e1 = new Empleado();
        e1.setId(1L);
        e1.setNombre("Ana");
        e1.setApellido("García");
        e1.setEdad(30);
        e1.setTelefono("1111");
        e1.setPuesto("Cajera");
        e1.setCreatedAt(LocalDateTime.now().minusDays(1));
        e1.setUpdatedAt(LocalDateTime.now().minusHours(2));

        Empleado e2 = new Empleado();
        e2.setId(2L);
        e2.setNombre("Juan");
        e2.setApellido("Pérez");
        e2.setEdad(28);
        e2.setTelefono("2222");
        e2.setPuesto("Repositor");
        e2.setCreatedAt(LocalDateTime.now().minusHours(10));
        e2.setUpdatedAt(LocalDateTime.now().minusHours(1));

        when(empleadoService.listarTodos()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/empleados/detalles"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalles_empleados"))
                .andExpect(model().attribute("EmpleadosComercio", hasSize(2)))
                .andExpect(model().attribute("UltimoEmpleado", hasProperty("id", is(2L))));
    }

    @Test
    void mostrarFormularioNuevo_devuelveEmpleadoVacioYVistaFormulario() throws Exception {
        mockMvc.perform(get("/empleados/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleado_form"))
                .andExpect(model().attributeExists("Empleado"))
                .andExpect(model().attribute("Titulo", "Nuevo Empleado"));
    }

    @Test
    void mostrarFormularioEdicion_buscaEmpleadoPorIdYDevuelveVista() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setId(5L);
        empleado.setNombre("Carlos");

        when(empleadoService.buscarPorId(5L)).thenReturn(empleado);

        mockMvc.perform(get("/empleados/editar/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleado_form"))
                .andExpect(model().attribute("Empleado", hasProperty("id", is(5L))))
                .andExpect(model().attribute("Titulo", "Editar Empleado"));
    }

    @Test
    void guardarEmpleado_redirigeAlListadoYLlamaAlServicio() throws Exception {
        // Aquí no nos importa el retorno de guardar, solo que se invoque
        mockMvc.perform(post("/empleados/guardar")
                        .param("nombre", "Laura")
                        .param("apellido", "López")
                        .param("edad", "25")
                        .param("telefono", "123456")
                        .param("puesto", "Vendedora"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empleados/detalles"));

        // Verificamos que se haya llamado al servicio al menos una vez
        verify(empleadoService, times(1)).guardar(org.mockito.ArgumentMatchers.any(Empleado.class));
    }

    @Test
    void eliminarEmpleado_redirigeAlListadoYLlamaAlServicio() throws Exception {
        mockMvc.perform(post("/empleados/eliminar/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empleados/detalles"));

        verify(empleadoService, times(1)).eliminar(10L);
    }
}
