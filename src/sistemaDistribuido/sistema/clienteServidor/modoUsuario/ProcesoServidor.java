package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;


/**
 * @Nombre: Rodriguez Haro Ricardo
 * @seccion: D04
 * @No: Practica 1
 * Modificado para Practica 1
 */

public class ProcesoServidor extends Proceso {

    /**
     *
     */
    public ProcesoServidor(Escribano esc) {
        super(esc);
        start();
    }

    /**
     *
     */
    public void run() {
        imprimeln("Inicio de proceso: Servidor.");
        byte[] solServidor = new byte[1024];
        byte[] respServidor;
        byte dato;
        while (continuar()) {
            Nucleo.receive(dameID(), solServidor);
            imprimeln("Invocando a receive()");

            dato = solServidor[8];
            imprimeln("Procesando peticion recibida del cliente");
            imprimeln("El cliente solicito: " + comandoCadena(dato));

            //desempacar datos relativos a la operacion
            String accionDesempacada = "";
            for (int i = 10; i < solServidor.length; i++) {
                accionDesempacada += (char) solServidor[i];
            }


            accionDesempacada = accionDesempacada.trim();
            imprimeln("El cliente envio: " + accionDesempacada);

            respServidor = new byte[1024];
            respServidor[0] = (byte) (dato);
            Pausador.pausa(1000);  //sin esta linea es posible que Servidor solicite send antes que Cliente solicite receive
            //imprimeln("enviando respuesta");
            imprimeln("Generando mensaje a ser enviado, llenando los campos necesarios");
            String envioRespuesta = ejecutarComando(dato, accionDesempacada);

            //empacar respuesta
            for (int i = 0; i < envioRespuesta.length(); i++) {
                respServidor[i + 8] = (byte) envioRespuesta.charAt(i);
            }

            imprimeln("Senialamiento al nucleo para envio de mensaje");
            Nucleo.send(0, respServidor);
            imprimeln("Fin del proceso");
        }//fin de while
    }//fin del metodo run

    private String escribir(String cadena) {

        String respuesta = "";
        String[] arreglo = cadena.split("-");
        //posicion 0 = NombreArchivo
        //posicion 1 = contenido a escribir
        try {
            File archivo = new File(arreglo[0]);

            if (!archivo.exists()) {
                respuesta = "Error al escribir el archivo";
            } else {

                FileWriter fw = new FileWriter(arreglo[0], true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.append(arreglo[1] + "\n");

                bw.close();
                respuesta = "Se escribio el archivo correctamente";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return respuesta;
    }//fin del metodo escribir

    private String leer(String cadena) {

        String respuesta = "Error al leer el archivo";
        String[] arreglo = cadena.split("-");
        String lineaArchivo = "";
        //posicion 0 = NombreArchivo
        //posicion 1 = numero linea a leer
        int numeroLinea = Integer.parseInt(arreglo[1]);
        try {
            FileReader fr = new FileReader(arreglo[0]);
            BufferedReader br = new BufferedReader(fr);

            int contadorLinea = 1;
            while ((lineaArchivo = br.readLine()) != null) {
                if (contadorLinea == numeroLinea) {
                    System.out.println("Tu linea es: " + lineaArchivo);
                    respuesta = "Archivo leido correctamente: " + lineaArchivo;
                }
                contadorLinea++;
            }//fin de while

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return respuesta;

    }

    private String eliminar(String nombreArchivo) {

        String respuesta = "";
        File archivo = new File(nombreArchivo);
        boolean eliminado = archivo.delete();

        if (eliminado) {
            System.out.println("Archivo eliminado");
            respuesta = "Archivo Eliminado correctamente";
        } else {
            System.out.println("No se elimino");
            respuesta = "Error al eliminar el archivo";
        }

        return respuesta;
    }

    private String crear(String nombreArchivo) {

        String respuesta = "";
        File archivo = new File(nombreArchivo);
        boolean archivoCreado = false;

        try {
            archivoCreado = archivo.createNewFile();
        }//fin de try
        catch (IOException ex) {
            ex.printStackTrace();
        }//fin de catch

        if (archivoCreado) {
            System.out.println("Archivo creado: " + archivo.getPath());
            respuesta = "Archivo creado correctamente";
        }//fin de if
        else {
            System.out.println("Archivo no creado");
            respuesta = "No se pudo crear el archivo";
        }

        return respuesta;
    }

    private String comandoCadena(byte comando) {
        String comandocadena = "";

        switch (comando) {
            case 0:
                comandocadena = "Crear";
                break;

            case 1:
                comandocadena = "Eliminar";
                break;

            case 2:
                comandocadena = "Leer";
                break;

            case 3:
                comandocadena = "Escribir";
                break;

        }//fin de switch

        return comandocadena;
    }//fin del metodo comandoCadena

    private String ejecutarComando(byte comando, String accion) {

        String respuesta = "";

        switch (comando) {
            case 0:
                respuesta = crear(accion);
                break;

            case 1:
                respuesta = eliminar(accion);
                break;

            case 2:
                respuesta = leer(accion);
                break;

            case 3:
                respuesta = escribir(accion);
                break;

        }//fin de switch

        return respuesta;

    }//fin del metodo ejecutarComando
}//fin de la clase ProcesoServidor
