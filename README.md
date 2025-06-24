# Proyecto-Atrapar-al-Gato
Repositorio para proyecto

## Ejecutando la aplicación
Antes que todo, hay que asegurarse de generar el jar de la aplicación.
Para esto se ejecuta
 
```bash
mvn clean package
```


Esto genera el archivo _atrapar-al-gato-1.0.0.jar_, el que se copia a una carpeta que se defina previamente, en el caso del proyecto se usa la carpeta del usuario del sistema operativo Windows.

Se debe abrir la consola e ir a la carpeta donde se ha copiado el archivo y posteriormente se debe ejecutar la siguiente línea de comando:

```bash
java -jar atrapar-al-gato-1.0.0.jar
```
El resultado esperado es el que se muestra en la siguiente imagen:

![Consola de Ejecución](consola.jpeg)

## Visualizar datos en la Base de Datos

Springboot provee de una consola de administración de la base de datos empotrada H2.
1. La primera información  que se obtiene es el puerto por donde Springboot atienede la aplicación: Tomcat initialized with port 8080 (http).
2. La segunda información es la url a la consola y a la base de datos: H2 console available at '/h2-console'. Database available at 'jdbc:h2:file:./data/atrapar-al-gato-db

Con esto es posible visualizar la información del juego.
Hay que realizar la conexión ingresando en el navegador a la [h2-console](http://localhost:8080/h2-console)

![H2-Console](h2-console.png)

Ud debe establecer el valor del "JDBC URL" con el valor jdbc:h2:file:./data/atrapar-al-gato-db. Luego debe presionar el botón Test Connection y posteriomente Connect.

El resultado esperado es algo como lo que se muestra en la siguiente imagen.

![H2-Console-Datos](h2-console-result.png)


## Jugar

Para comenzar a jugar se debe acceder al juego usando un navegador compatible que soporte javascript. Para ello debe ingresar al [juego](http://localhost:8080).

Se sugiere establecer el nombre del jugador al inicio.

Durante el juego puede que gane el gato o el jugador, el sistema presenta un diálogo consultando si quiere grabar el resultado. Es importante grabar el resultado para que se pueda ver posteriormente las puntauciones con nombre de jugador.

