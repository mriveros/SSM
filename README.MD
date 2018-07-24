Pasos pare realizar Deploy de Aplicación SSM

1- Clonar Proyecto git desde la siguiente URL: https://github.com/dgticstp/ssm
2- Desde la Aplicación Android Studio (https://developer.android.com/studio/?hl=es-419)
a. Abrir Proyecto.
b. Navegar hasta la ubicación de la carpeta del Proyecto y seleccionarlo.
3- Crear archivo google-services.json siguiendo los siguientes pasos(opcional):
a. Ir a la consola de Firebasde (https://console.firebase.google.com)
b. Seleccionar “Agregar Proyecto”
c. Seleccionar “Agrega Firebase a tu app Android”
d. Registrar la aplicacion con los datos solicitados
e. Descargar el archivo google-services.json
f. Copiar el archivo google-service.json en la carpeta app del proyecto android
4- Editar el archivo URLs.java que se encuentra dentro de la carpeta del Proyecto dentro de la
siguiente ruta: ssm/app/src/main/java/com/stp/ssm/http/
a. Se debe configurar el webservice de envio y recepción de datos de la siguiente manera:
i. Ejemplo: https://url-tu-webservice/servicio
5- Crear el ejecutable de la aplicación (.apk) siguiendo los siguientes pasos:
a. En Android Studio seleccionar Build>Build(APKs)
