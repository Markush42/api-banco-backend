package com.informaticonfig.spring.myapp1.springboot_applications.test;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.informaticonfig.spring.myapp1.springboot_applications.models.Empleado;
import com.informaticonfig.spring.myapp1.springboot_applications.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // usa application-test.properties -> H2
class EmpleadoControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void setup() {
        // Limpiamos la base para que cada test arranque en cero
        empleadoRepository.deleteAll();
    }

    @Test
    void contextoCarga_yListadoVacioFunciona() throws Exception {
        mockMvc.perform(get("/empleados/detalles"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalles_empleados"))
                .andExpect(model().attribute("EmpleadosComercio", hasSize(0)))
                .andExpect(model().attribute("UltimoEmpleado", nullValue()));
    }

    @Test
    void flujoConDatos_listaEmpleadosYUltimoEmpleado() throws Exception {
        // Insertamos un empleado directamente en la BD (H2)
        Empleado empleado = new Empleado();
        empleado.setNombre("Eva");
        empleado.setApellido("Martínez");
        empleado.setEdad(32);
        empleado.setTelefono("555-1234");
        empleado.setPuesto("Supervisora");
        empleado.setDireccion("Calle Falsa 123");
        empleado.setCreatedAt(LocalDateTime.now().minusDays(1));
        empleado.setUpdatedAt(LocalDateTime.now());

        empleadoRepository.save(empleado);

        mockMvc.perform(get("/empleados/detalles"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalles_empleados"))
                .andExpect(model().attribute("EmpleadosComercio", hasSize(1)))
                .andExpect(model().attribute("UltimoEmpleado", hasProperty("nombre", is("Eva"))));
    }
}
