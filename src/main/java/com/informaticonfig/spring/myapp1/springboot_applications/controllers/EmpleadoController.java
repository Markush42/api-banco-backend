package com.informaticonfig.spring.myapp1.springboot_applications.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.informaticonfig.spring.myapp1.springboot_applications.models.Empleado;
import com.informaticonfig.spring.myapp1.springboot_applications.service.EmpleadoService;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    // Listado principal
    @GetMapping("/detalles")
    public String listarEmpleados(Model model) {
        List<Empleado> empleados = empleadoService.listarTodos();

        // Nunca confiar en null por si cambia la implementación
        if (empleados == null) {
            empleados = Collections.emptyList();
        }

        // Último empleado (o null si no hay ninguno)
        Empleado ultimoEmpleado = empleados.isEmpty()
                ? null
                : empleados.get(empleados.size() - 1);

        model.addAttribute("EmpleadosComercio", empleados);
        model.addAttribute("UltimoEmpleado", ultimoEmpleado);
        model.addAttribute("Titulo", "Detalle de empleados");

        // Plantilla Thymeleaf: src/main/resources/templates/detalles_empleados.html
        return "detalles_empleados";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Empleado empleado = empleadoService.buscarPorId(id);
        model.addAttribute("Empleado", empleado);
        model.addAttribute("Titulo", "Editar Empleado");
        // Plantilla formulario: src/main/resources/templates/empleado_form.html
        return "empleado_form";
    }

    // Mostrar formulario de creación
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("Empleado", new Empleado());
        model.addAttribute("Titulo", "Nuevo Empleado");
        return "empleado_form";
    }

    // Guardar cambios de un empleado (crear/actualizar)
    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute("Empleado") Empleado empleado) {
        empleadoService.guardar(empleado);
        return "redirect:/empleados/detalles";
    }

    // Eliminar empleado
    @PostMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return "redirect:/empleados/detalles";
    }
}
