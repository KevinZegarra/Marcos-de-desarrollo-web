package com.gym.service;

import com.gym.dto.ClienteDTO;
import com.gym.entity.Cliente;

import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    List<Cliente> buscar(String termino);

    Cliente buscarPorId(Long id);

    Cliente crear(ClienteDTO dto);

    Cliente actualizar(Long id, ClienteDTO dto);

    void eliminar(Long id);

    Cliente renovar(Long clienteId, Long planId, String metodoPago);

    void actualizarEstados();

    long countActivos();

    long countPorVencer();

    long countVencidos();

    List<Cliente> clientesProximosAVencer();
}
