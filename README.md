## FancyMenus
Es un plugin de código abierto hecho para crear menús de manera optimizada.

## Ejemplos
[Menú basico de ejemplo](https://github.com/dattavw/FastMenus/blob/master/examples/example.yml)

## Selectores de Objetivo
| Selector         | Descripción                                                                 |
|------------------|-----------------------------------------------------------------------------|
| `@a`             | Selecciona a **todos** los jugadores actualmente en línea.                   |
| `@p`             | Selecciona al jugador **más cercano** a la entidad que ejecuta la acción.    |
| `@r`             | Selecciona a un jugador **aleatorio** de los que están en línea.            |
| `@e`             | Selecciona a **todas las entidades** en el mundo. Sin embargo, la implementación actual filtra y solo incluye jugadores. |
| `[nombre_jugador]` | Selecciona al jugador con el **nombre** especificado.                      |

## Acciones Disponibles

| Acción         | Formato (Ejemplo)                                                     | Descripción Resumida                                                                 |
|----------------|-----------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `console`      | `console: <comando>`                                                  | Ejecuta un comando desde la consola del servidor.                                    |
| `player_chat`  | `player_chat: <mensaje>`                                                | Simula que el jugador envía un mensaje al chat.                                      |
| `player_cmd`   | `player_cmd: <comando>`                                                 | Ejecuta un comando como si el jugador lo hubiera escrito.                            |
| `player_msg`   | `player_msg: [<objetivo>;]<mensaje>`                                   | Envía un mensaje privado a uno o varios jugadores (o al sender si no se especifica). |
| `player_sound` | `player_sound: [<objetivo>;]<sonido>;[volumen];[tono]`                 | Reproduce un sonido para uno o varios jugadores (o al sender si no se especifica).    |
| `player_title` | `player_title: [<objetivo>;]<título>;<subtítulo>;[fadeIn];[stay];[fadeOut]` | Muestra un título y subtítulo en la pantalla de uno o varios jugadores (o al sender si no se especifica). |

