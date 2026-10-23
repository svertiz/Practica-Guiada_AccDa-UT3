package com.acda.nexoreservas.model.service;

import com.acda.nexoreservas.exception.BusinessException;
import com.acda.nexoreservas.exception.ResourceNotFoundException;
import com.acda.nexoreservas.model.entity.Cliente;
import com.acda.nexoreservas.model.repository.ClienteRepository;
import org.hibernate.usertype.BaseUserTypeSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class ClienteService {
    // TODO bloque 4: lista blanca para Sort, con dirección ASC/DESC.
    private static final Set<String> ORDENES = Set.of("nombre", "apellidos", "email", "activo");

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscar(String q, Boolean activo, String orden, String direccion) {
        // TODO bloques 3 y 4: consulta por texto/estado y ordenación.
        return clientes.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtener(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente solicitado no existe"));
    }

    @Transactional(readOnly = true)
    public List<Cliente> todos() {
        return clientes.findAll();
    }

    @Transactional(readOnly = true)
    public long total() {
        return clientes.count();
    }

    @Transactional
    public Cliente guardar(Cliente entrada) {
        // TODO bloque 1: validar, normalizar y comprobar email duplicado.
        if (entrada != null && !entrada.getNombre().isBlank() && !entrada.getApellidos().isBlank()){
            String email = entrada.getEmail().trim().toLowerCase();
            String[] emailTroceado = email.split("@");
            if (email.contains("@") && emailTroceado.length == 2 && entrada.getActivo() != null){
                if (!clientes.existsByEmailIgnoreCaseAndIdNot(email, entrada.getId())){
                    entrada.setEmail(email);
                    entrada.setNombre(entrada.getNombre().trim());
                    entrada.setApellidos(entrada.getApellidos().trim());
                    entrada.setTelefono(entrada.getTelefono().trim());
                    clientes.save(entrada);
                    clientes.flush();
                } else {
                    throw new BusinessException("El email ya existe en otra cuenta");
                }
            } else {
                throw new BusinessException("Formato inválido del email");
            }
        }
        return entrada;
    }

    @Transactional
    public void eliminar(Long id) {
        // TODO bloque 1: obtener, comprobar reglas y eliminar.
        Cliente cliente = obtener(id);
        if (cliente != null){
            clientes.delete(cliente);
            clientes.flush();
        }
        // En bloque 2, impedir el borrado si hay reservas asociadas.
        throw new BusinessException("Eliminar cliente: operación pendiente de la práctica");
    }
}
