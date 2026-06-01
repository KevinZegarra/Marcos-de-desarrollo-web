# AY Fitness Gym — Sistema de Gestión Web

Sistema completo Spring Boot + Thymeleaf + MySQL para la gestión de un gimnasio.

---

## Tecnologías

- Java 17 + Spring Boot 3.2
- Spring MVC + Thymeleaf
- Spring Security (BCrypt)
- JPA / Hibernate
- MySQL 8
- Bootstrap 5.3 + Bootstrap Icons
- Chart.js 4

---

## Instalación paso a paso

### 1. Prerequisitos
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 2. Crear la base de datos MySQL

```sql
CREATE DATABASE ayfitnessgym
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ayfitnessgym?...
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
```

### 4. Correr el proyecto

```bash
mvn spring-boot:run
```

Al iniciar, el `DataInitializer` crea automáticamente:
- **3 planes**: Básico (S/79), Pro (S/129), Elite (S/189)
- **Usuario admin**: usuario `admin` / contraseña `admin123`

### 5. Acceder

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/` | Landing page pública |
| `http://localhost:8080/login` | Login admin |
| `http://localhost:8080/admin/dashboard` | Panel admin (requiere login) |

---

## Personalización

### Logo del gimnasio
Coloca el logo en:
```
src/main/resources/static/images/logo/logo.png
```
Se usa automáticamente en la landing y el login.

### Imágenes de galería
```
src/main/resources/static/images/galeria/
  sala-pesas.jpg
  cardio.jpg
  clases.jpg
  etc.
```
Luego en `index.html` reemplaza los `gallery-slot` con:
```html
<img th:src="@{/images/galeria/sala-pesas.jpg}" class="gallery-img" alt="Sala de Pesas"/>
```

### Redes sociales y WhatsApp
En `fragments/header.html` (footer) y en `index.html`, busca:
```
https://wa.me/51999000000
```
Y reemplaza con tu número real de WhatsApp (formato internacional sin +).

Para Facebook/Instagram/TikTok, busca los comentarios `⚙️` en los templates.

### Google Maps
En `index.html`, sección `#ubicacion`, reemplaza el div `map-placeholder` con el `<iframe>` que obtienes de Google Maps → Compartir → Insertar mapa.

### Precios de los planes
Los precios se gestionan desde la BD. En producción puedes cambiarlos en:
```sql
UPDATE planes SET precio = 89.00 WHERE nombre = 'Básico';
```
O agrega una sección de gestión de planes en el panel admin (próxima versión).

### Cambiar contraseña del admin
```sql
-- Genera el hash BCrypt con cualquier herramienta online
-- o con Spring Security:
UPDATE usuarios SET password = '$2a$10$HASH_AQUI' WHERE username = 'admin';
```

---

## Estructura del proyecto

```
src/main/java/com/gym/
├── AyFitnessGymApplication.java
├── config/
│   ├── DataInitializer.java      ← crea planes y admin al iniciar
│   └── SecurityConfig.java       ← Spring Security
├── controller/
│   ├── HomeController.java       ← landing + login
│   └── AdminController.java      ← panel completo
├── dto/
│   ├── ClienteDTO.java
│   └── DashboardDTO.java
├── entity/
│   ├── Cliente.java
│   ├── Pago.java
│   ├── Plan.java
│   └── Usuario.java
├── repository/
│   ├── ClienteRepository.java
│   ├── PagoRepository.java
│   ├── PlanRepository.java
│   └── UsuarioRepository.java
├── security/
│   └── GymUserDetailsService.java
└── service/
    ├── ClienteService.java
    ├── DashboardService.java
    ├── PagoService.java
    └── PlanService.java

src/main/resources/
├── application.properties
├── db-init.sql                   ← referencia SQL + datos de prueba
├── static/css/styles.css
└── templates/
    ├── index.html                ← landing page
    ├── auth/login.html
    ├── fragments/header.html     ← navbar, footer, sidebar
    └── admin/
        ├── dashboard.html
        ├── clientes.html
        ├── cliente-form.html
        ├── pagos.html
        └── reportes.html
```

---

## Flujo de uso

1. **Registrar cliente** → `/admin/clientes/nuevo`
   - Se elige nombre, plan y fecha de inicio
   - Se registra el pago automáticamente
   - La fecha de vencimiento se calcula sola (inicio + 30 días)

2. **Renovar membresía** → botón 🔄 en la tabla de clientes
   - Elige el plan y método de pago
   - Si ya venció, cuenta desde hoy
   - Si aún está vigente, extiende desde la fecha actual de vencimiento

3. **Dashboard** → métricas en tiempo real desde la BD
4. **Reportes** → gráficos Chart.js de ingresos y miembros

---

## Credenciales por defecto

> ⚠️ **Cambia la contraseña en producción**

| Campo | Valor |
|-------|-------|
| Usuario | `admin` |
| Contraseña | `admin123` |
