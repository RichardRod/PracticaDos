package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 *
 * @Nombre: Rodriguez Haro Ricardo
 * @seccion: D04
 * @No: Practica 1
 * Modificado para Practica 1
 */

public class ProcesoCliente extends Proceso {

    private byte codigoOperacion;
    private String datosOperacion;

    public ProcesoCliente(Escribano esc) {
        super(esc);
        start();
    }

    public void run() {

        int idOrigen = dameID();
        int idDestino = 248;

        imprimeln("Inicio de proceso: Cliente.");
        imprimeln("Esperando datos para continuar.");

        Nucleo.suspenderProceso();

        byte[] solCliente = new byte[1024];
        byte[] respCliente = new byte[1024];
        byte dato;

        //empacar CODOP
        solCliente[8] = codigoOperacion;

        //empacar datos relativos a la operacion
        for (int i = 0; i < datosOperacion.length(); i++) {
            solCliente[i + 10] = (byte) datosOperacion.charAt(i);
        }

        byte[] idOrigenEmpacado = empacar(idOrigen);
        byte[] idDestinoEmpacado = empacar(idDestino);

        solCliente[0] = idOrigenEmpacado[0];
        solCliente[1] = idOrigenEmpacado[1];
        solCliente[2] = idOrigenEmpacado[2];
        solCliente[3] = idOrigenEmpacado[3];

        solCliente[4] = idDestinoEmpacado[0];
        solCliente[5] = idDestinoEmpacado[1];
        solCliente[6] = idDestinoEmpacado[2];
        solCliente[7] = idDestinoEmpacado[3];

        imprimeln("Senialamiento al nucleo para envio de mensaje");

        Nucleo.send(idDestino, solCliente);

        imprimeln("Generando mensaje a ser enviado, llenando los campos necesarios");

        Nucleo.receive(dameID(), respCliente);
        imprimeln("Invocando a receive()");
        dato = respCliente[8];
        imprimeln("Procesando respuesta recibida del servidor");

        //desempacar datos relativos a la operacion
        String respuestaDesempacada = "";
        for (int i = 0; i < respCliente.length; i++) {
            respuestaDesempacada += (char) respCliente[i];
        }
        respuestaDesempacada = respuestaDesempacada.trim();

        imprimeln("Respuesta del servidor: " + respuestaDesempacada);
        imprimeln("Fin del proceso");

    }//fin del metodo run

    public void enviarComando(byte CODOP) {

        switch (CODOP) {
            case 0: //crear
                codigoOperacion = 0;
                break;

            case 1: //eliminar
                codigoOperacion = 1;
                break;

            case 2: //leer
                codigoOperacion = 2;
                break;

            case 3: //escribir
                codigoOperacion = 3;
                break;

        }//fin de switch
    }//fin del metodo enviarComando

    public String recibirDatosOperacion(String datosOperacion) {
        return this.datosOperacion = datosOperacion;
    }//fin del metodo recibirDatosOperacion

    public byte[] empacar(int valor)
    {
        byte[] arreglo = new byte[4];

        arreglo[0] = (byte) (valor >> 24);
        arreglo[1] = (byte) (valor >> 16);
        arreglo[2] = (byte) (valor >> 8);
        arreglo[3] = (byte) (valor);

        return arreglo;
    }//fin del metodo empacar
}
