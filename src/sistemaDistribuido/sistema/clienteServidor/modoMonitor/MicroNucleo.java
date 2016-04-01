package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

/**
 *
 */
public final class MicroNucleo extends MicroNucleoBase{

	private static MicroNucleo nucleo=new MicroNucleo();
	private Hashtable<Integer, byte[]> tablaRecepcion;
	private Hashtable<Integer, TransmisionProcesos> tablaEmision;

	/**
	 *
	 */
	private MicroNucleo()
	{
		tablaRecepcion  = new Hashtable<>();
		tablaEmision = new Hashtable<>();
	}//fin del constructor

	/**
	 *
	 */
	public final static MicroNucleo obtenerMicroNucleo(){
		return nucleo;
	}

	/*---Metodos para probar el paso de mensajes entre los procesos cliente y servidor en ausencia de datagramas.
    Esta es una forma incorrecta de programacion "por uso de variables globales" (en este caso atributos de clase)
    ya que, para empezar, no se usan ambos parametros en los metodos y fallaria si dos procesos invocaran
    simultaneamente a receiveFalso() al reescriir el atributo mensaje---*/
	byte[] mensaje;

	public void sendFalso(int dest,byte[] message){
		System.arraycopy(message,0,mensaje,0,message.length);
		notificarHilos();  //Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
	}

	public void receiveFalso(int addr,byte[] message){
		mensaje=message;
		suspenderProceso();
	}
	/*---------------------------------------------------------*/

	/**
	 *
	 */
	protected boolean iniciarModulos(){
		return true;
	}

	/**
	 * aqui debes hacer la magia de envio de mensajes por la red como en el chat
	 */
	protected void sendVerdadero(int dest,byte[] message){
		sendFalso(dest,message);
		imprimeln("El proceso invocante es el "+super.dameIdProceso());

		//lo siguiente aplica para la practica #2
		ParMaquinaProceso pmp = dameDestinatarioDesdeInterfaz();

		if(tablaEmision.containsKey(dest))
		{
			message = empacarMensaje(tablaEmision.get(new Integer(dest)).getId(), message);
			enviarMensaje(tablaEmision.get(new Integer(dest)).getIp(), message);

			imprimeln("Enviando mensaje a IP="+tablaEmision.get(new Integer(dest)).getIp()+" ID="+tablaEmision.get(new Integer(dest)));
			tablaEmision.remove(dest);
		}//fin de if
		else
		{
			message = empacarMensaje(pmp.dameID(), message);
			enviarMensaje(pmp.dameIP(), message);
			imprimeln("Enviando mensaje a IP="+pmp.dameIP()+" ID="+pmp.dameID());

		}//fin de else

		//suspenderProceso();   //esta invocacion depende de si se requiere bloquear al hilo de control invocador

	}//fin del metodo sendVerdadero

	/**
	 *
	 */
	protected void receiveVerdadero(int addr,byte[] message){
		receiveFalso(addr,message);
		tablaRecepcion.put(new Integer(addr), message);

		//el siguiente aplica para la pr�ctica #2
		//suspenderProceso();
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en practica 5
	 */
	protected void sendVerdadero(String dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void sendNBVerdadero(int dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void receiveNBVerdadero(int addr,byte[] message){
	}

	/**
	 *
	 */
	public void run(){

		//variables locales
		byte[] mensaje = new byte[1024];
		byte[] origen = new byte[4];
		byte[] destino = new byte[4];
		byte[] datos;

		String ip;
		Proceso procesoDestino;
		DatagramPacket packet = new DatagramPacket(mensaje, mensaje.length);






		while(seguirEsperandoDatagramas()){



		}
	}//fin del metodo run

	private void enviarMensaje(String ip, byte[] mensaje)
	{
		DatagramPacket packet;

		try
		{
			packet = new DatagramPacket(mensaje, mensaje.length, InetAddress.getByName(ip), damePuertoRecepcion());
			dameSocketEmision().send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}//fin de metodo enviarMensaje

	private byte[] empacarMensaje(int destino, byte[] mensaje)
	{
		byte[] mensajeEmpacado = mensaje;
		byte[] origen = empacar(super.dameIdProceso());
		byte[] destinoAux = empacar(destino);

		for(int i = 0; i < origen.length; i++)
		{
			mensajeEmpacado[i] = origen[i];
		}//fin de for

		for(int i = origen.length, j = 0; i < (destinoAux.length + origen.length); i++, j++)
		{
			mensajeEmpacado[i] = destinoAux[j];
		}//fin de for

		return mensajeEmpacado;
	}//fin del metodo empacarMensaje

	public byte[] empacar(int valor)
	{
		byte[] arreglo = new byte[4];

		arreglo[0] = (byte) (valor >> 24);
		arreglo[1] = (byte) (valor >> 16);
		arreglo[2] = (byte) (valor >> 8);
		arreglo[3] = (byte) (valor);

		return arreglo;
	}//fin del metodo empacar

	public byte[] empacar(short valor)
	{
		byte[] arreglo = new byte[4];

		arreglo[0] = (byte) (valor >> 8);
		arreglo[1] = (byte) (valor);

		return arreglo;
	}//fin del metodo empacar
}

class TransmisionProcesos
{
	//atributos
	private String ip;
	private int id;

	//constructor
	TransmisionProcesos(String ip, int id){
		this.ip = ip;
		this.id = id;
	}//fin del constructor

	public String getIp() {
		return ip;
	}

	public int getId() {
		return id;
	}
}