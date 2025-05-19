# FancyMenus

Es un plugin de código abierto hecho para crear menús de manera optimizada.

## Ejemplos
[Menú basico de ejemplo](https://github.com/dattavw/FastMenus/blob/master/examples/example.yml)

## Selectores de Objetivo

| Selector          | Descripción                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `@a`              | Selecciona a **todos** los jugadores actualmente en línea.                  |
| `@p`              | Selecciona al jugador **más cercano** a la entidad que ejecuta la acción.   |
| `@r`              | Selecciona a un jugador **aleatorio** de los que están en línea.            |
| `@e`              | Selecciona a **todas las entidades** en el mundo. Sin embargo, la implementación actual filtra y solo incluye jugadores. |
| `[nombre_jugador]` | Selecciona al jugador con el **nombre** especificado.                      |

## Acciones Disponibles

| Acción        | Formato (Ejemplo)                                       | Descripción Resumida                                                                   |
|---------------|-------------------------------------------------------|----------------------------------------------------------------------------------------|
| `console`     | `console: <comando>`                                  | Ejecuta un comando desde la consola del servidor.                                       |
| `player_chat` | `player_chat: <mensaje>`                              | Simula que el jugador envía un mensaje al chat.                                         |
| `player_cmd`  | `player_cmd: <comando>`                               | Ejecuta un comando como si el jugador lo hubiera escrito.                              |
| `player_msg`  | `player_msg: [<objetivo>;]<mensaje>`                  | Envía un mensaje privado a uno o varios jugadores (o al sender si no se especifica). |
| `player_sound`| `player_sound: [<objetivo>;]<sonido>;[volumen];[tono]` | Reproduce un sonido para uno o varios jugadores (o al sender si no se especifica).   |
| `player_title`| `player_title: [<objetivo>;]<título>;<subtítulo>;[fadeIn];[stay];[fadeOut]` | Muestra un título y subtítulo en la pantalla de uno o varios jugadores (o al sender si no se especifica). |

## Flags

| Flag              | Descripción                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `headPlayer`      | Especifica el nick de un jugador de Minecraft para mostrar su cabeza.       |
| `headUrl`         | Especifica la URL de una imagen para usar como skin de cabeza personalizada. |
| `customModelData` | Permite utilizar un valor numérico para referenciar un modelo personalizado. |
| `hideFlags`         | Especificar el estado para ocultar atributos del item (true o false). |

**Por ejemplo asi se veria adentro de un item:**
```
flags:
      headPlayer: "datta"
      headUrl: "http://textures.minecraft.net/texture/841b91c5205bcc64e7f848e5db386990fb1cd5173f330f5d1439618563e7c10b"
      customModelData: 1
      hideAll: true
```
