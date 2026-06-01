package com.gym.service;

import com.gym.dto.ClienteDTO;
import com.gym.entity.Cliente;
import com.gym.entity.Cliente.EstadoCliente;
import com.gym.entity.Pago;
import com.gym.entity.Plan;
import com.gym.repository.ClienteRepository;
import com.gym.repository.PagoRepository;
import com.gym.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PlanRepository planRepository;
    private final PagoRepository pagoRepository;

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> buscar(String termino) {
        return clienteRepository
            .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(Objects.requireNonNull(id, "Cliente id es obligatorio"))
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
    }

    @Override
    @Transactional
    public Cliente crear(ClienteDTO dto) {
        Long planId = Objects.requireNonNull(dto.getPlanId(), "Plan id es obligatorio");
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setCorreo(dto.getCorreo());
        cliente.setPlan(plan);
        cliente.setFechaInscripcion(dto.getFechaInscripcion());
        cliente.setFechaVencimiento(dto.getFechaInscripcion().plusDays(plan.getDuracionDias()));
        cliente.setEstado(EstadoCliente.ACTIVO);

        cliente = clienteRepository.save(cliente);

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setPlan(plan);
        pago.setMonto(plan.getPrecio());
        pago.setFechaPago(dto.getFechaInscripcion());
        pago.setFechaInicio(dto.getFechaInscripcion());
        pago.setFechaVencimiento(cliente.getFechaVencimiento());
        pago.setEstado(Pago.EstadoPago.PAGADO);
        pago.setMetodoPago(dto.getMetodoPago());
        pagoRepository.save(pago);

        return cliente;
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);
        Long planId = Objects.requireNonNull(dto.getPlanId(), "Plan id es obligatorio");
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setCorreo(dto.getCorreo());
        cliente.setPlan(plan);

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        clienteRepository.deleteById(Objects.requireNonNull(id, "Cliente id es obligatorio"));
    }

    @Override
    @Transactional
    public Cliente renovar(Long clienteId, Long planId, String metodoPago) {
        Cliente cliente = buscarPorId(clienteId);
        Long planIdNotNull = Objects.requireNonNull(planId, "Plan id es obligatorio");
        Plan plan = planRepository.findById(planIdNotNull)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        LocalDate inicio = cliente.getFechaVencimiento() != null
            && cliente.getFechaVencimiento().isAfter(LocalDate.now())
            ? cliente.getFechaVencimiento()
            : LocalDate.now();

        LocalDate nuevoVencimiento = inicio.plusDays(plan.getDuracionDias());
        cliente.setPlan(plan);
        cliente.setFechaVencimiento(nuevoVencimiento);
        cliente.setEstado(EstadoCliente.ACTIVO);
        cliente = clienteRepository.save(cliente);

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setPlan(plan);
        pago.setMonto(plan.getPrecio());
        pago.setFechaPago(LocalDate.now());
        pago.setFechaInicio(inicio);
        pago.setFechaVencimiento(nuevoVencimiento);
        pago.setEstado(Pago.EstadoPago.PAGADO);
        pago.setMetodoPago(metodoPago);
        pagoRepository.save(pago);

        return cliente;
    }

    @Override
    @Transactional
    public void actualizarEstados() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7);

        List<Cliente> todos = clienteRepository.findAll();
        for (Cliente c : todos) {
            if (c.getFechaVencimiento() == null) continue;
            if (c.getFechaVencimiento().isBefore(hoy)) {
                c.setEstado(EstadoCliente.VENCIDO);
            } else if (c.getFechaVencimiento().isBefore(limite)) {
                c.setEstado(EstadoCliente.POR_VENCER);
            } else {
                c.setEstado(EstadoCliente.ACTIVO);
            }
        }
        clienteRepository.saveAll(todos);
    }

    @Override
    public long countActivos() {
        return clienteRepository.countClientesActivos(LocalDate.now());
    }

    @Override
    public long countPorVencer() {
        return clienteRepository
            .findClientesProximosAVencer(LocalDate.now(), LocalDate.now().plusDays(7))
            .size();
    }

    @Override
    public long countVencidos() {
        return clienteRepository.findClientesVencidos(LocalDate.now()).size();
    }

    @Override
    public List<Cliente> clientesProximosAVencer() {
        return clienteRepository.findClientesProximosAVencer(LocalDate.now(), LocalDate.now().plusDays(7));
    }
}
