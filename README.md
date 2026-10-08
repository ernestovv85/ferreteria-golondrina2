# Evidencia de Aprendizaje U1 · POO III

## Ferretería La Golondrina · Catálogo de Estados (Etapa 2)

Aplicación de escritorio en Java para administrar el **Catálogo de Estados** de la ferretería La Golondrina. Permite **registrar, consultar, modificar y eliminar** estados. La información se guarda en un archivo de texto para que se conserve entre ejecuciones.

| | |
|---|---|
| **Alumno** | Ernesto Velázquez Velázquez |
| **Asignatura** | Programación Orientada a Objetos III |
| **Unidad** | 1 · Evidencia de aprendizaje |
| **Etapa** | 2 · Administración de catálogos |
| **Módulo asignado** | Catálogo de Estados |

---

## Funcionalidades

### Etapa 1 (se conserva)
- **Registro de estados** (`Catálogos → Estados`, `Ctrl+E`): formulario con *Clave*, *Nombre del estado* y *Capital*. Cada registro nuevo se agrega a `estados.txt`.
- Tabla de **estados registrados** junto al formulario.
- **Validaciones al guardar:**
  - Campos vacíos.
  - Longitud máxima: 4 caracteres para la clave y 60 para los demás campos.
  - El carácter separador `|` no se permite.
  - La clave y el nombre no pueden estar duplicados.

### Etapa 2 (nuevo)
- **Barra de título** con el módulo asignado, la leyenda **"Etapa 2"** y el nombre completo del alumno.
- **Consulta** (`Catálogos → Consulta`, `Ctrl+K`): lee `estados.txt` y muestra todos los registros en una tabla. Cada fila tiene los botones **Modificar** y **Eliminar**.
- **Eliminar**:
  - Abre una ventana de confirmación con las opciones **Confirmar** y **Cancelar**.
  - **Confirmar** elimina el registro del archivo de texto y actualiza la tabla.
  - **Cancelar**, o cerrar la ventana, conserva el registro sin cambios.
- **Modificar**:
  - Abre una ventana con los datos actuales del registro para editarlos.
  - Antes de guardar, valida que **ningún campo esté vacío**. Los campos vacíos se marcan en rojo.
  - También aplica las reglas de longitud, separador y duplicados. Al revisar duplicados se ignora el propio registro.
  - Guarda el cambio en el archivo de texto y actualiza la fila correspondiente en la tabla.
- La pantalla de **registro** vuelve a leer el archivo cada vez que se abre, así que muestra los cambios hechos en la Consulta.

---

## Tecnologías

- **Java 17**
- **Spring Boot 4.1.1**: inyección de dependencias (`@Service`, `@Repository`, `@Component`) y configuración.
- **Java Swing**: interfaz gráfica.
- **Maven**: incluye el wrapper `mvnw`, así que no hace falta instalar Maven.

---

## Estructura del proyecto

```
DPO3_U1_EA_ERVV/
├── pom.xml
├── mvnw / mvnw.cmd
├── estados.txt                         ← archivo de datos (se crea al primer registro)
└── src/main/
    ├── resources/
    │   ├── application.properties
    │   └── images/estados.png          ← imagen de la pantalla de inicio
    └── java/mx/golondrina/ferreteria/DPO3_U1_EA_ERVV/
        ├── FerreteriaApplication.java  ← punto de entrada (Spring Boot + Swing)
        ├── model/
        │   └── Estado.java             ← record: clave, nombre, capital
        ├── repository/
        │   └── EstadoRepository.java   ← lectura y escritura del archivo de texto
        ├── service/
        │   ├── EstadoService.java      ← reglas de negocio y validaciones
        │   └── ValidacionException.java
        └── ui/
            ├── VentanaPrincipal.java   ← JFrame, menú y navegación entre pantallas
            ├── PanelInicio.java        ← pantalla de bienvenida
            ├── PanelEstados.java       ← registro de estados (Etapa 1)
            ├── PanelConsulta.java      ← consulta, modificar y eliminar (Etapa 2)
            ├── DialogoModificar.java   ← ventana de edición (Etapa 2)
            ├── ColumnaBoton.java       ← botones dentro de las celdas del JTable (Etapa 2)
            ├── AppInfo.java            ← textos de la aplicación (título, alumno, módulo)
            └── Tema.java               ← paleta de colores
```

### Arquitectura por capas

```
   UI (Swing)          →        Servicio          →        Repositorio         →   estados.txt
PanelEstados                 EstadoService               EstadoRepository
PanelConsulta                · validaciones              · listar()
DialogoModificar             · duplicados                · guardar()
                             · registrar/modificar/      · actualizar()
                               eliminar                  · eliminar()
```

- La **interfaz** solo muestra datos y captura acciones. Nunca accede directamente al archivo.
- El **servicio** concentra las reglas de negocio, de modo que registrar y modificar comparten exactamente las mismas validaciones.
- El **repositorio** es la única clase que lee o escribe `estados.txt`.

---

## Formato del archivo de datos

`estados.txt` está codificado en UTF-8 y tiene un registro por línea, con los campos separados por `|`:

```
CLAVE|NOMBRE|CAPITAL
```

Ejemplo:

```
JAL|Jalisco|Guadalajara
NL|Nuevo León|Monterrey
YUC|Yucatán|Mérida
```

- Al **registrar**, la nueva línea se agrega al final del archivo.
- Al **modificar o eliminar**, el archivo se reescribe completo y conserva el orden de los registros. Primero se escribe un archivo temporal (`estados.txt.tmp`) que luego reemplaza al original, para que una falla a mitad del proceso no deje el catálogo dañado.

---

## Ejecución

### Desde IntelliJ IDEA
Abre el proyecto y ejecuta la clase `FerreteriaApplication`.

### Desde la terminal
```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Por defecto, `estados.txt` se lee y se escribe en la carpeta desde donde se ejecuta la aplicación (la raíz del proyecto). Para usar otra ruta, define la propiedad `catalogo.estados.archivo`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--catalogo.estados.archivo=datos/estados.txt
```

---

## Guía rápida de uso

1. **Registrar:** `Catálogos → Estados`. Captura la clave, el nombre y la capital, y presiona **Guardar** (o Enter).
2. **Consultar:** `Catálogos → Consulta`. Aparecen todos los estados guardados en el archivo.
3. **Modificar:** en la Consulta, presiona **Modificar** en la fila deseada, edita los datos y presiona **Guardar cambios** (Esc cancela).
4. **Eliminar:** en la Consulta, presiona **Eliminar** y elige **Confirmar** o **Cancelar**.
5. **Salir:** `Archivo → Salir` (`Ctrl+Q`) o cierra la ventana. En ambos casos se pide confirmación.
