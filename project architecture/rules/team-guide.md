# Agenda App — Guía equipo

Esta guía resume las decisiones principales del proyecto para que todo el equipo programe siguiendo la misma estructura.

## 1. Arquitectura
```text
ConsoleUI
   ↓
Service
   ↓
Repository
   ↓
SqlDao
   ↓
MySQL
```

Reglas:
- UI: entrada/salida por consola.
- Service: lógica de aplicación.
- Repository: interfaz de persistencia.
- SqlDao: implementación JDBC/MySQL.
- Las entidades no conocen consola, SQL ni MySQL.
- Los Services dependen de Repository, nunca de SqlDao.
- Usar inyección por constructor.

## 2. Estructura
```text
src/main/java/com/agenda/
├── application/
│   ├── Main.java
│   └── menu/
├── common/
│   ├── exception/
│   ├── persistence/
│   └── utils/
├── task/
│   ├── cli/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── service/
├── note/
│   ├── cli/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── service/
├── event/
│   ├── cli/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── service/
└── infrastructure/
    └── sql/
        └── dao/
```

Tests:
```text
src/test/java/com/agenda/
```

## 3. Naming
- Clases/interfaces/records/enums: `PascalCase`
- Métodos/variables: `lowerCamelCase`
- Enum values: `UPPER_SNAKE_CASE`


## 4. Clases principales

### Task
```text
Task
TaskId
TaskPriority
TaskStatus
TaskDto
TaskConsoleUI
TaskService
TaskRepository
TaskSqlDao
```

`TaskPriority`: `LOW`, `MEDIUM`, `HIGH`

`TaskStatus`: `PENDING`, `COMPLETED`

Creación:
- texto obligatorio
- prioridad por defecto `MEDIUM`
- estado inicial `PENDING`
- `createdAt` automático
- `expirationDate` es opcional (`null`)
- si se informa, no puede ser anterior a la fecha actual
- esta regla se aplica tanto al crear como al actualizar una `Task`
- `completedAt` inicialmente `null`

Métodos de dominio:
```text
markAsCompleted()
updateDetails(...)
```

### Estado de Task
- `status` no se modifica mediante `updateDetails(...)`
- una `Task` pasa de `PENDING` a `COMPLETED` mediante `markAsCompleted()`
- `markAsCompleted()` establece automáticamente `completedAt`
- si la `Task` ya está completada, `markAsCompleted()` no modifica de nuevo `completedAt`

### Note
```text
Note
NoteId
NoteDto
NoteConsoleUI
NoteService
NoteRepository
NoteSqlDao
```

Reglas:
- siempre pertenece a una Task existente
- guarda `TaskId`, no `Task`
- una Task puede tener 0 Notes
- No puede existir una Note sin Task
- crear una Note nunca crea una Task

Métodos:
```text
updateContent(...)
changeTask(...)
```

### Event
```text
Event
EventId
RecurrenceType
EventDto
EventConsoleUI
EventService
EventRepository
EventSqlDao
```

`RecurrenceType`:
```text
NONE
ANNUAL
WEEKLY
MONTHLY
```

Event guarda:
```text
List<TaskId>
```

Métodos:
```text
updateDetails(...)
configureRecurrence(...)
addTask(...)
```

## 5. IDs
Los IDs los genera MySQL.

El backend:
- no genera ID
- no usa contadores propios
- no pide ID al crear entidades
- recupera el ID generado por MySQL tras el INSERT

IDs:
```text
TaskId
NoteId
EventId
```

Reglas:
- todos los IDs deben ser mayores que `0`
- `TaskId`, `NoteId` y `EventId` deben rechazar valores `<= 0`

## 6. Repositories
```text
TaskRepository
- save(Task)
- findById(TaskId)
- findAll()
- deleteById(TaskId)

NoteRepository
- save(Note)
- findById(NoteId)
- findAll()
- findByTaskId(TaskId)
- deleteById(NoteId)

EventRepository
- save(Event)
- findById(EventId)
- findAll()
- deleteById(EventId)
```

Implementaciones:
```text
TaskSqlDao
NoteSqlDao
EventSqlDao
```

## 7. Services
Solo un Service principal por feature:
```text
TaskService
NoteService
EventService
```

No crear Services o UseCases separados por cada operación salvo que lo decidamos más adelante.

Métodos principales:
```text
TaskService
- create(...)
- update(...)
- delete(...)
- complete(...)
- findAll()
- findPending()
- findCompleted()
- filterByPriority(...)
- filterByStatus(...)
- filterByDate(...)
- sortTasks(...)

NoteService
- create(...)
- update(...)
- delete(...)
- findAll()
- findByTaskId(...)

EventService
- create(...)
- update(...)
- delete(...)
- findUpcoming(...)
- addTask(...)
- configureRecurrence(...)
- addObserver(...)
- checkUpcomingEvents(...)
```

## 8. DTOs
Solo:
```text
TaskDto
NoteDto
EventDto
```

Preferiblemente `record`.

No contienen lógica de negocio, SQL ni acceso a repositorios.

## 9. Relaciones y borrado

### Task → Note
```text
Task 1 <-- 0..* Note
```

Al borrar una Task:
```text
se borran todas sus Notes
```

Preferencia en BD:
```text
ON DELETE CASCADE
```

### Event → Task
- un Event puede tener varias Tasks
- una Task puede existir sin Event
- una Task solo puede pertenecer a un Event a la vez
- una Task puede cambiar su asociación de un Event a otro

Al borrar una Task vinculada:
```text
se borra la Task
se borran sus Notes
se elimina la asociación
el Event permanece
```

Al borrar un Event:
```text
se borra el Event
se borran sus Tasks asociadas
se borran también las Notes de esas Tasks
```

## 10. Patrones
### Repository
```text
Service → Repository ← SqlDao
```

### Strategy
```text
TaskSortStrategy
PrioritySortStrategy
StatusSortStrategy
DateSortStrategy
```

### Factory
```text
RecurrenceFactory
RecurrencePolicy
NoRecurrencePolicy
AnnualRecurrencePolicy
WeeklyRecurrencePolicy
MonthlyRecurrencePolicy
```

### Observer
```text
EventObserver
NotificationService
```

## 11. Responsabilidad por capa

### ConsoleUI
Puede:
- mostrar menús
- leer input
- validar formato
- pedir confirmaciones
- crear DTOs/IDs
- mostrar mensajes

No puede:
- ejecutar SQL
- usar DAOs directamente
- contener lógica de negocio

### Service
Puede:
- validar reglas
- consultar repositories
- modificar entidades
- coordinar operaciones
- mapear entidad ↔ DTO

No puede:
- usar JDBC
- ejecutar SQL
- leer Scanner directamente

### DAO
Puede:
- ejecutar SQL
- usar JDBC
- mapear filas ↔ entidades
- recuperar IDs generados por MySQL

No puede:
- mostrar menús
- aplicar reglas de negocio

## 12. Excepciones
```text
TaskNotFoundException
NoteNotFoundException
EventNotFoundException
```

Crear nuevas solo si representan una regla real.

## 13. Testing
- JUnit 6
- Tests en `src/test/java/com/agenda/`
- Naming: `<ClassName>Test`

Ejemplos:
```text
shouldCompleteTaskWhenTaskExists
shouldRejectNoteWhenTaskDoesNotExist
```

Unit tests:
- dominio
- Services
- patterns

Integration tests:
- JDBC
- MySQL
- persistencia

## 14. Git
Rama de integración:
```text
dev
```

Flujo:
```text
dev
↓
rama propia
↓
commits
↓
push
↓
Pull Request a dev
↓
review
↓
merge
```

Commits:
```text
feat:
fix:
refactor:
test:
docs:
chore:
```

## 15. Antes de crear una clase nueva
Comprobar:
1. ¿Ya existe una clase que debe asumir esa responsabilidad?
2. ¿Encaja en la arquitectura?
3. ¿Está en el package correcto?
4. ¿Respeta el naming?
5. ¿Es realmente necesaria?

## 16. Pendiente de revisión
No implementar por suposición:

### Proximidad de fecha
Pendiente definir exactamente `filterByDate(...)`.

### Ordenación
```text
LOW / MEDIUM / HIGH
PENDING / COMPLETED
```

## Regla final
```text
NO ADIVINAR.
```

Revisar:
```text
historia activa
→ UML definitivo
→ arquitectura
→ código actual en dev
→ reglas del proyecto
```

Si sigue sin estar definido, decidimos cuando lleguemos.
