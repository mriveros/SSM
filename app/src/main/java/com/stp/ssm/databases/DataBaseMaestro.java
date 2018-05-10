package com.stp.ssm.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseMaestro extends SQLiteOpenHelper {


	public static String TABLE_BENEFICIARIO = "Beneficiario";
	public static String TABLE_BENEFICIARIO_ID = "_id";
	public static String TABLE_BENEFICIARIO_REGISTRO_ID = "registro_id";
	public static String TABLE_BENEFICIARIO_NOMBRE = "nombre";
	public static String TABLE_BENEFICIARIO_APELLIDO = "apellido";
	public static String TABLE_BENEFICIARIO_NOMBRE_VERIF = "nombre_verificado";
	public static String TABLE_BENEFICIARIO_APELLIDO_VERIF = "apellido_verificado";
	public static String TABLE_BENEFICIARIO_DOCUMENTO = "documento";
	public static String TABLE_BENEFICIARIO_TIPO = "tipo";
	public static String TABLE_BENEFICIARIO_USUARIO = "usuario";
	public static String TABLE_BENEFICIARIO_PROYECTO = "proyecto";
	public static String TABLE_BENEFICIARIO_LONG = "longitud";
	public static String TABLE_BENEFICIARIO_LAT = "latitud";
	public static String TABLE_BENEFICIARIO_PRESC = "presicion";
	public static String TABLE_BENEFICIARIO_DPTO = "departamento";
	public static String TABLE_BENEFICIARIO_DISTRITO = "distrito";
	public static String TABLE_BENEFICIARIO_LOCALIDAD = "localidad";
	public static String TABLE_BENEFICIARIO_EST = "estado";
	public static String TABLE_BENEFICIARIO_ENV = "envio";
	public static String TABLE_BENEFICIARIO_JEFE = "es_jefe";
	public static String TABLE_BENEFICIARIO_FAMILY = "familia";
	private static String TABLE_BENEFICIARIO_CREATE = "CREATE TABLE " + TABLE_BENEFICIARIO + " (" +
			TABLE_BENEFICIARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			TABLE_BENEFICIARIO_REGISTRO_ID + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_NOMBRE + " TEXT," +
			TABLE_BENEFICIARIO_APELLIDO + " TEXT DEFAULT ''," +
			TABLE_BENEFICIARIO_NOMBRE_VERIF + " TEXT DEFAULT ''," +
			TABLE_BENEFICIARIO_APELLIDO_VERIF + " TEXT," +
			TABLE_BENEFICIARIO_DOCUMENTO + " TEXT UNIQUE," +
			TABLE_BENEFICIARIO_TIPO + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_USUARIO + " TEXT," +
			TABLE_BENEFICIARIO_PROYECTO + " INTEGER," +
			TABLE_BENEFICIARIO_LONG + " TEXT," +
			TABLE_BENEFICIARIO_LAT + " TEXT," +
			TABLE_BENEFICIARIO_PRESC + " REAL," +
			TABLE_BENEFICIARIO_DPTO + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_DISTRITO + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_LOCALIDAD + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_JEFE+ " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_EST + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_FAMILY + " INTEGER DEFAULT 0," +
			TABLE_BENEFICIARIO_ENV + " INTEGER DEFAULT 0);";


	public static String TABLE_INSTITUCIONES = "instituciones";
	public static String TABLE_INSTITUCIONES_ID = "_id";
	public static String TABLE_INSTITUCIONES_NOM = "nombre";
	public static String TABLE_INSTITUCIONES_COD = "codigo";
	private static String TABLE_INSTITUCIONES_CREATE = "CREATE TABLE " + TABLE_INSTITUCIONES + " (" +
													   TABLE_INSTITUCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													   TABLE_INSTITUCIONES_NOM + " TEXT," +
													   TABLE_INSTITUCIONES_COD + " INTEGER UNIQUE);";

	public static String TABLE_PROYECTOS = "proyectos";
	public static String TABLE_PROYECTOS_ID = "_id";
	public static String TABLE_PROYECTOS_NOM = "nombre";
	public static String TABLE_PROYECTOS_COD = "codigo";
	public static String TABLE_PROYECTOS_INST = "institucion";
	public static String TABLE_PROYECTOS_TIPO = "tipo";
	public static String TABLE_PROYECTOS_CANT_MIN_IMG = "cant_min_img";
	public static String TABLE_PROYECTOS_ALTA_DESTINATARIO = "alta_destinatario";
	public static String TABLE_PROYECTOS_ENTIDAD_RELEVAR = "entidad_relevar";
	private static String TABLE_PROYECTOS_CREATE = "CREATE TABLE " + TABLE_PROYECTOS + " (" +
												   TABLE_PROYECTOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
												   TABLE_PROYECTOS_NOM + " TEXT," +
												   TABLE_PROYECTOS_INST + " INTEGER," +
												   TABLE_PROYECTOS_COD + " 	INTEGER UNIQUE," +
												   TABLE_PROYECTOS_TIPO + " INTEGER DEFAULT 0," +
			                                       TABLE_PROYECTOS_CANT_MIN_IMG + " INTEGER DEFAULT 1," +
										           TABLE_PROYECTOS_ALTA_DESTINATARIO + " INTEGER DEFAULT 1," +
												   TABLE_PROYECTOS_ENTIDAD_RELEVAR + " TEXT);";


	public static String TABLE_MOTIVOS = "Motivos";
	public static String TABLE_MOTIVOS_ID = "_id";
	public static String TABLE_MOTIVOS_CODIGO = "codigo_motivo";
	public static String TABLE_MOTIVOS_DESCRIPCION = "descripcion";
	public static String TABLE_MOTIVOS_COD_FORMULARIO = "cod_Formulario";
	public static String TABLE_MOTIVOS_FORMULARIO_DESCRIP = "descripcion_formulario";
	public static String TABLE_MOTIVOS_ID_PROYECTO = "id_proyecto";
	public static String TABLE_MOTIVOS_HAS_FORMULARIO = "has_formulario";
	private static String TABLE_MOTIVOS_CREATE = "CREATE TABLE " + TABLE_MOTIVOS + " (" +
			TABLE_MOTIVOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			TABLE_MOTIVOS_CODIGO + " INTEGER UNIQUE," +
			TABLE_MOTIVOS_DESCRIPCION + " TEXT," +
			TABLE_MOTIVOS_COD_FORMULARIO + " INTEGER," +
			TABLE_MOTIVOS_HAS_FORMULARIO + " INTEGER DEFAULT 0," +
			TABLE_MOTIVOS_ID_PROYECTO + " INTEGER," +
			TABLE_MOTIVOS_FORMULARIO_DESCRIP + " TEXT);";


	public static String TABLE_SECCIONES = "Secciones";
	public static String TABLE_SECCIONES_ID = "_id";
	public static String TABLE_SECCIONES_MOTIVO = "cod_motivo";
	public static String TABLE_SECCIONES_COD = "cod_seccion";
	public static String TABLE_SECCIONES_DESCRIP = "descripcion";
	public static String TABLE_SECCIONES_TIPO = "tipo";
	public static String TABLE_SECCIONES_TOTALIZABLE = "totalizable";
	public static String TABLE_SECCIONES_CONDICIONABLE = "condicionable";
	public static String TABLE_SECCIONES_MULTIMEDIA = "multimedia";
	private static String TABLE_SECCIONES_CREATE = "CREATE TABLE "+TABLE_SECCIONES+" (" +
												    TABLE_SECCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													TABLE_SECCIONES_MOTIVO + " INTEGER," +
													TABLE_SECCIONES_COD + " INTEGER UNIQUE,"+
												    TABLE_SECCIONES_DESCRIP + " TEXT," +
													TABLE_SECCIONES_TIPO +" INTEGER DEFAULT 0," +
												    TABLE_SECCIONES_TOTALIZABLE + " INTEGER DEFAULT 0," +
													TABLE_SECCIONES_CONDICIONABLE + " INTEGER DEFAULT 0," +
												    TABLE_SECCIONES_MULTIMEDIA + " TEXT);";


	public static String TABLE_VISITA = "Visitas";
	public static String TABLE_VISITA_ID = "_id";
	public static String TABLE_VISITA_DOCBENEFICIARIO = "docBeneficiario";
	public static String TABLE_VISITA_HORAINICIO = "horainicio";
	public static String TABLE_VISITA_HORAFIN = "horafin";
	public static String TABLE_VISITA_CODMOTIVO = "codMotivo";
	public static String TABLE_VISITA_LONG = "longitud";
	public static String TABLE_VISITA_LAT = "latitud";
	public static String TABLE_VISITA_PRES = "presicion";
	public static String TABLE_VISITA_OBS = "observacion";
	public static String TABLE_VISITA_PROYECTO = "idproyecto";
	public static String TABLE_VISITA_USER = "usuario";
	public static String TABLE_VISITA_EST = "estado";
	public static String TABLE_VISITA_ENV = "enviado";
	public static String TABLE_VISITA_TIME = "tiempo";
	public static String TABLE_VISITA_DPTO = "departamento";
	public static String TABLE_VISITA_DIST = "distrito";
	public static String TABLE_VISITA_LOC = "localidad";
	public static String TABLE_VISITA_CORRECTO = "correcto";
	public static String TABLE_VISITA_ORIGINAL = "original";
	public static String TABLE_VISITA_ID_KEY = "id_key";
	private static String TABLE_VISITA_CREATE = "CREATE TABLE " + TABLE_VISITA + " (" +
			TABLE_VISITA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			TABLE_VISITA_DOCBENEFICIARIO + " TEXT," +
			TABLE_VISITA_HORAINICIO + " NUMERIC," +
			TABLE_VISITA_HORAFIN + " NUMERIC," +
			TABLE_VISITA_CODMOTIVO + " INTEGER," +
			TABLE_VISITA_LONG + " TEXT DEFAULT '0'," +
			TABLE_VISITA_LAT + " TEXT DEFAULT '0'," +
			TABLE_VISITA_PRES + " REAL DEFAULT 0," +
			TABLE_VISITA_OBS + " TEXT DEFAULT ''," +
			TABLE_VISITA_PROYECTO + " INTEGER,"+
			TABLE_VISITA_USER + " TEXT,"+
			TABLE_VISITA_TIME + " INTEGER DEFAULT 0,"+
			TABLE_VISITA_EST + " INTEGER DEFAULT 0," +
			TABLE_VISITA_DPTO + " INTEGER DEFAULT 0," +
			TABLE_VISITA_DIST + " INTEGER DEFAULT 0," +
			TABLE_VISITA_LOC + " INTEGER DEFAULT 0," +
			TABLE_VISITA_ENV + " INTEGER DEFAULT 0," +
			TABLE_VISITA_CORRECTO + " INTEGER DEFAULT 1," +
			TABLE_VISITA_ORIGINAL + " INTEGER DEFAULT 1," +
			TABLE_VISITA_ID_KEY + " TEXT DEFAULT '');";


	public static String TABLE_CAPTURAS = "Capturas";
	public static String TABLE_CAPTURAS2 = "Capturas2";
	public static String TABLE_CAPTURAS_ID = "_id";
	public static String TABLE_CAPTURAS_IDVISITA = "idVisita";
	public static String TABLE_CAPTURAS_DOCBENEFICIARIO = "docbeneficiario";
	public static String TABLE_CAPTURAS_PATH = "path";
	public static String TABLE_CAPTURAS_FECHA = "fecha";
	public static String TABLE_CAPTURAS_NUMERO = "numero";
	public static String TABLE_CAPTURAS_ORIGEN = "origen";
	public static String TABLE_CAPTURAS_HASH = "hash";
	public static String TABLE_CAPTURAS_NRO_ENVIOS = "nro_envios";
	public static String TABLE_CAPTURAS_EST = "estado";
	public static String TABLE_CAPTURAS_SYNC = "sync";
	private static String TABLE_CAPTURAS_CREATE_1 = "CREATE TABLE " + TABLE_CAPTURAS + " (" +
												     TABLE_CAPTURAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
												     TABLE_CAPTURAS_IDVISITA + " INTEGER DEFAULT 0," +
													 TABLE_CAPTURAS_DOCBENEFICIARIO + " TEXT DEFAULT ''," +
													 TABLE_CAPTURAS_PATH + " TEXT," +
													 TABLE_CAPTURAS_FECHA + " NUMERIC," +
													 TABLE_CAPTURAS_NUMERO + " INTEGER DEFAULT 0,"+
												     TABLE_CAPTURAS_ORIGEN + " INTEGER DEFAULT 0,"+
													 TABLE_CAPTURAS_HASH + " TEXT DEFAULT '',"+
													 TABLE_CAPTURAS_NRO_ENVIOS + " INTEGER DEFAULT 0,"+
													 TABLE_CAPTURAS_EST + " INTEGER DEFAULT 0," +
													 TABLE_CAPTURAS_SYNC + " INTEGER DEFAULT 0);";
	private static String TABLE_CAPTURAS_CREATE_2 = "CREATE TABLE " + TABLE_CAPTURAS2 + " (" +
													 TABLE_CAPTURAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													 TABLE_CAPTURAS_IDVISITA + " INTEGER DEFAULT 0," +
													 TABLE_CAPTURAS_DOCBENEFICIARIO + " TEXT DEFAULT ''," +
													 TABLE_CAPTURAS_PATH + " TEXT," +
													 TABLE_CAPTURAS_FECHA + " NUMERIC," +
													 TABLE_CAPTURAS_NUMERO + " INTEGER DEFAULT 0,"+
													 TABLE_CAPTURAS_EST + " INTEGER DEFAULT 0);";


	public static String TABLE_ADJ = "Adjuntos";
	public static String TABLE_ADJ_ID = "_id";
	public static String TABLE_ADJ_IDVISITA = "idVisita";
	public static String TABLE_ADJ_PATH = "path";
	public static String TABLE_ADJ_FECHA = "fecha";
	public static String TABLE_ADJ_EST = "estado";
	private static String TABLE_ADJ_CREATE = "CREATE TABLE " + TABLE_ADJ + " (" +
											  TABLE_ADJ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
											  TABLE_ADJ_IDVISITA + " INTEGER," +
											  TABLE_ADJ_PATH + " TEXT," +
											  TABLE_ADJ_FECHA + " NUMERIC," +
											  TABLE_ADJ_EST + " INTEGER DEFAULT 0);"; //0=>no enviado;1=>Enviado


	public static String TABLE_PREGUNTAS = "Preguntas";
	public static String TABLE_PREGUNTAS_ID = "_id";
	public static String TABLE_PREGUNTAS_ID_SECCION = "id_seccion";
	public static String TABLE_PREGUNTAS_ID_PREG = "id_pregunta";
	public static String TABLE_PREGUNTAS_TXT = "texto";
	public static String TABLE_PREGUNTAS_TIPO = "tipo";
	public static String TABLE_PREGUNTAS_INDICE = "indice";
	public static String TABLE_PREGUNTAS_requerido = "requerido";
	public static String TABLE_PREGUNTAS_condicion = "condicion";
	public static String TABLE_PREGUNTAS_estado = "estado";
	public static String TABLE_PREGUNTAS_TOTALIZABLE = "totalizable";
	public static String TABLE_PREGUNTAS_VISIBLE = "visible";
	private static String TABLE_PREGUNTAS_CREATE = "CREATE TABLE " + TABLE_PREGUNTAS + " (" +
													TABLE_PREGUNTAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													TABLE_PREGUNTAS_ID_SECCION + " INTEGER,"+
													TABLE_PREGUNTAS_ID_PREG + " INTEGER UNIQUE,"+
													TABLE_PREGUNTAS_TXT + " TEXT," +
													TABLE_PREGUNTAS_TIPO + " INTEGER," +
													TABLE_PREGUNTAS_INDICE + " INTEGER DEFAULT 0," +
													TABLE_PREGUNTAS_requerido + " INTEGER DEFAULT 0," +
												    TABLE_PREGUNTAS_condicion + " TEXT DEFAULT ''," +
													TABLE_PREGUNTAS_TOTALIZABLE + " INTEGER DEFAULT 0," +
													TABLE_PREGUNTAS_VISIBLE + " INTEGER DEFAULT 0," +
												    TABLE_PREGUNTAS_estado + " INTEGER DEFAULT 1);";


	public static String TABLE_PREGUNTAS_CONDICION = "preguntas_condicion";
	public static String TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONANTE = "id_preg_condicionate";
	public static String TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA = "id_preg_condicionada";
	public static String TABLE_PREGUNTAS_CONDICION_COND = "condicion";
	public static String TABLE_PREGUNTAS_CONDICION_VALOR = "valor";
	private static String TABLE_PREGUNTAS_CONDICION_CREATE = "CREATE TABLE " + TABLE_PREGUNTAS_CONDICION + " (" +
													          TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONANTE + " INTEGER," +
															  TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA + " INTEGER," +
															  TABLE_PREGUNTAS_CONDICION_COND + " INTEGER," +
															  TABLE_PREGUNTAS_CONDICION_VALOR + " TEXT);";


	public static String TABLE_SECCION_CONDICION = "seccion_condicion";
	public static String TABLE_SECCION_CONDICION_SEC_CONDICIONADA = "id_sec_condicionada";
	public static String TABLE_SECCION_CONDICION_SEC_SIGUIENTE = "id_sec_siguiente";
	public static String TABLE_SECCION_CONDICION_PREG_CONDICIONANTE = "id_preg_condicionate";
	public static String TABLE_SECCION_CONDICION_COND = "condicion";
	public static String TABLE_SECCION_CONDICION_VALOR = "valor";
	private static String TABLE_SECCION_CONDICION_CREATE = "CREATE TABLE " + TABLE_SECCION_CONDICION + " (" +
														   TABLE_SECCION_CONDICION_SEC_CONDICIONADA + " INTEGER," +
														   TABLE_SECCION_CONDICION_SEC_SIGUIENTE + " INTEGER," +
														   TABLE_SECCION_CONDICION_PREG_CONDICIONANTE + " INTEGER," +
														   TABLE_SECCION_CONDICION_COND + " INTEGER," +
														   TABLE_SECCION_CONDICION_VALOR + " TEXT);";

	public static String TABLE_POSRESPUESTAS = "Posible_Respuestas";
	public static String TABLE_POSRESPUESTAS_ID = "_id";
	public static String TABLE_POSRESPUESTAS_IDPREGUNTA = "id_pregunta";
	public static String TABLE_POSRESPUESTAS_IDRESPUESTA = "id_respuesta";
	public static String TABLE_POSRESPUESTAS_OPCION = "respuesta";
	private static String TABLE_POSRESPUESTAS_CREATE = "CREATE TABLE "+TABLE_POSRESPUESTAS+ " (" +
													    TABLE_POSRESPUESTAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
														TABLE_POSRESPUESTAS_IDPREGUNTA + " INTEGER," +
														TABLE_POSRESPUESTAS_IDRESPUESTA + " INTEGER UNIQUE," +
														TABLE_POSRESPUESTAS_OPCION + " TEXT);";


	public static String TABLE_RESPUESTAS = "Respuestas";
	public static String TABLE_RESPUESTAS_ID = "_id";
	public static String TABLE_RESPUESTAS_ID_PREG = "id_pregunta";
	public static String TABLE_RESPUESTAS_ID_VIST = "id_visita";
	public static String TABLE_RESPUESTAS_ID_POSIBLE_RESP = "id_posible_respuesta";
	public static String TABLE_RESPUESTAS_TEXT_RESP = "txt_respuesta";
	public static String TABLE_RESPUESTAS_BENEFICIARIODOC = "documento_encuestado";
	public static String TABLE_RESPUESTAS_PREG_TIPO = "tipo";
	public static String TABLE_RESPUESTAS_NRO_SUBFORM = "nro_subform";
	private static String TABLE_RESPUESTAS_CREATE = "CREATE TABLE "+TABLE_RESPUESTAS+ " (" +
												     TABLE_RESPUESTAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
													 TABLE_RESPUESTAS_ID_PREG + " INTEGER," +
													 TABLE_RESPUESTAS_ID_VIST + " INTEGER," +
													 TABLE_RESPUESTAS_BENEFICIARIODOC + " TEXT," +
													 TABLE_RESPUESTAS_ID_POSIBLE_RESP + " INTEGER DEFAULT 0," +
													 TABLE_RESPUESTAS_TEXT_RESP + " TEXT," +
													 TABLE_RESPUESTAS_PREG_TIPO + " INTEGER," +
													 TABLE_RESPUESTAS_NRO_SUBFORM + " INTEGER DEFAULT 0);";

	public static String TABLE_UBICACIONES = "Ubicacion";
	public static String TABLE_UBICACIONES_ID = "_id";
	public static String TABLE_UBICACIONES_LONG = "longitud";
	public static String TABLE_UBICACIONES_LAT = "latitud";
	public static String TABLE_UBICACIONES_HORA_OBT = "hora_obtenido";
	public static String TABLE_UBICACIONES_HORA_GUARD = "hora_guardado";
	public static String TABLE_UBICACIONES_PROVEDOR = "proveedor";
	public static String TABLE_UBICACIONES_BATERIA = "bateria";
	public static String TABLE_UBICACIONES_ALTITUD = "altitud";
	public static String TABLE_UBICACIONES_PRECS = "precision";
	public static String TABLE_UBICACIONES_TIPO = "tipo";
	public static String TABLE_UBICACIONES_EST = "estado";
	public static String TABLE_UBICACIONES_USER = "usuario";
	private static String TABLE_UBICACIONES_CREATE = "CREATE TABLE "+TABLE_UBICACIONES+ " (" +
												      TABLE_UBICACIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													  TABLE_UBICACIONES_LONG + " TEXT DEFAULT '0'," +
													  TABLE_UBICACIONES_LAT + " TEXT DEFAULT '0'," +
													  TABLE_UBICACIONES_HORA_OBT + " NUMERIC," +
													  TABLE_UBICACIONES_HORA_GUARD + " NUMERIC," +
													  TABLE_UBICACIONES_PROVEDOR + " TEXT," +
													  TABLE_UBICACIONES_BATERIA + " INTEGER," +
													  TABLE_UBICACIONES_ALTITUD + " INTEGER," +
													  TABLE_UBICACIONES_PRECS + " INTEGER," +
													  TABLE_UBICACIONES_TIPO + " INTEGER," +
													  TABLE_UBICACIONES_USER + " TEXT," +
													  TABLE_UBICACIONES_EST + " INTEGER DEFAULT 0);";

	public static String TABLE_OPERADORA = "operadora";
	public static String TABLE_OPERADORA_ID = "_id";
	public static String TABLE_OPERADORA_NOM = "nom_operadora";
	public static String TABLE_OPERADORA_SERIAL_SIM = "serial_sim";
	public static String TABLE_OPERADORA_COUNTRYISO = "contryiso";
	public static String TABLE_OPERADORA_CODOPERADORA = "cod_operadora";
	public static String TABLE_OPERADORA_FECHA = "fecha";
	public static String TABLE_OPERADORA_ANDROIDID = "androidid";
	public static String TABLE_OPERADORA_ESTADO = "estado";
	public static String TABLE_OPERADORA_USUARIO = "usuario";
	private static String TABLE_OPERADORA_CREATE = "CREATE TABLE "+TABLE_OPERADORA+ " (" +
													TABLE_OPERADORA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
													TABLE_OPERADORA_NOM + " TEXT," +
												    TABLE_OPERADORA_SERIAL_SIM + " TEXT," +
													TABLE_OPERADORA_COUNTRYISO + " TEXT," +
													TABLE_OPERADORA_CODOPERADORA + " TEXT," +
													TABLE_OPERADORA_FECHA + " NUMERIC," +
													TABLE_OPERADORA_USUARIO + " TEXT," +
													TABLE_OPERADORA_ANDROIDID + " TEXT," +
													TABLE_OPERADORA_ESTADO +" INTEGER DEFAULT 0);";


	public static String TABLE_EVENTOS = "eventos";
	public static String TABLE_EVENTOS_ID = "_id";
	public static String TABLE_EVENTOS_FECHA = "fecha";
	public static String TABLE_EVENTOS_LON = "longitud";
	public static String TABLE_EVENTOS_LAT = "latitud";
	public static String TABLE_EVENTOS_PRES = "presicion";
	public static String TABLE_EVENTOS_descripcion = "descripcion";
	public static String TABLE_EVENTOS_USER = "usuario";
	public static String TABLE_EVENTOS_envio = "estado";
	private static String TABLE_EVENTOS_CREATE = "CREATE TABLE " + TABLE_EVENTOS+" ("+
												  TABLE_EVENTOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
												  TABLE_EVENTOS_FECHA +" NUMERIC," +
												  TABLE_EVENTOS_LON+" TEXT," +
												  TABLE_EVENTOS_LAT+" TEXT," +
											      TABLE_EVENTOS_PRES+" INTEGER," +
												  TABLE_EVENTOS_descripcion+" TEXT," +
												  TABLE_EVENTOS_USER+" TEXT," +
												  TABLE_EVENTOS_envio + " INTEGER DEFAULT 0);";

	public static String TABLE_CAPTURA_EVENTOS = "captura_evento";
	public static String TABLE_CAPTURA_EVENTOS_ID = "_id";
	public static String TABLE_CAPTURA_EVENTOS_IDEVENTO = "idevento";
	public static String TABLE_CAPTURA_EVENTOS_PATH = "path";
	public static String TABLE_CAPTURA_EVENTOS_FECHA = "fecha";
	public static String TABLE_CAPTURA_EVENTOS_envio = "estado";
	public static String TABLE_CAPTURA_EVENTOS_HASH = "hash";
	public static String TABLE_CAPTURA_EVENTOS_NRO_ENVIOS = "nro_envios";
	public static String TABLE_CAPTURA_EVENTOS_SYNC = "sync";
	private static String TABLE_CAPTURA_EVENTOS_CREATE = "CREATE TABLE " + TABLE_CAPTURA_EVENTOS+" ("+
														                   TABLE_CAPTURA_EVENTOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
																		   TABLE_CAPTURA_EVENTOS_IDEVENTO +" INTEGER," +
																		   TABLE_CAPTURA_EVENTOS_PATH+" TEXT," +
																	       TABLE_CAPTURA_EVENTOS_FECHA+" NUMERIC," +
																		   TABLE_CAPTURA_EVENTOS_HASH + " TEXT DEFAULT ''," +
																		   TABLE_CAPTURA_EVENTOS_NRO_ENVIOS + " INTEGER DEFAULT 0," +
																		   TABLE_CAPTURA_EVENTOS_envio + " INTEGER DEFAULT 0," +
																		   TABLE_CAPTURA_EVENTOS_SYNC + " INTEGER DEFAULT 0);";
	public static String TABLE_DEPARTAMENTOS = "departamentos";
	public static String TABLE_DEPARTAMENTOS_ID = "_id";
	public static String TABLE_DEPARTAMENTOS_NOMBRE = "nombre";
	private static String TABLE_DEPARTAMENTOS_CREATE = "CREATE TABLE "+TABLE_DEPARTAMENTOS+" (" +
																	   TABLE_DEPARTAMENTOS_ID + " INTEGER PRIMARY KEY," +
																	   TABLE_DEPARTAMENTOS_NOMBRE + " TEXT);";

	public static String TABLE_DISTRITOS = "distritos";
	public static String TABLE_DISTRITOS_ID = "_id";
	public static String TABLE_DISTRITOS_NOMBRE = "nombre";
	public static String TABLE_DISTRITOS_DPTO = "id_dpto";
	private static String TABLE_DISTRITOS_CREATE = "CREATE TABLE "+TABLE_DISTRITOS+" (" +
																   TABLE_DISTRITOS_ID + " INTEGER," +
																   TABLE_DISTRITOS_NOMBRE + " TEXT," +
																   TABLE_DISTRITOS_DPTO + " INTEGER," +
														          "PRIMARY KEY("+TABLE_DISTRITOS_ID+","+TABLE_DISTRITOS_DPTO+"));";

	public static String TABLE_RECORRIDO = "recorrido";
	public static String TABLE_RECORRIDO_ID = "_id";
	public static String TABLE_RECORRIDO_INI = "inicio";
	public static String TABLE_RECORRIDO_FIN = "fin";
	public static String TABLE_RECORRIDO_TIEMPO = "tiempo";
	public static String TABLE_RECORRIDO_USUARIO = "usuario";
	public static String TABLE_RECORRIDO_ENVIO = "estado";
	private static String TABLE_RECORRIDO_CREATE = "CREATE TABLE " + TABLE_RECORRIDO + " (" +
			                                                        TABLE_RECORRIDO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
																    TABLE_RECORRIDO_INI + " NUMERIC," +
																	TABLE_RECORRIDO_FIN + " NUMERIC," +
																	TABLE_RECORRIDO_TIEMPO + " INTEGER," +
																	TABLE_RECORRIDO_USUARIO + " TEXT," +
																	TABLE_RECORRIDO_ENVIO + " INTEGER DEFAULT 0);";
	public static String TABLE_EVTCELL = "eventos_telefono";
	public static String TABLE_EVTCELL_ID = "_id";
	public static String TABLE_EVTCELL_DESCRIP = "descripcion";
	public static String TABLE_EVTCELL_FECHA = "fecha";
	public static String TABLE_EVTCELL_USER = "usuario";
	public static String TABLE_EVTCELL_ENVIO = "estado";
	private static String TABLE_EVTCELL_CREATE = "CREATE TABLE " + TABLE_EVTCELL + " (" +
																   TABLE_EVTCELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
																   TABLE_EVTCELL_DESCRIP + " TEXT," +
																   TABLE_EVTCELL_FECHA + " NUMERIC," +
														           TABLE_EVTCELL_USER + " TEXT," +
																   TABLE_EVTCELL_ENVIO + " INTEGER DEFAULT 0);";

	public static String TABLE_NOTIFICACIONES = "notificaciones";
	public static String TABLE_NOTIFICACIONES_ID = "_id";
	public static String TABLE_NOTIFICACIONES_TOPIC = "topic";
	public static String TABLE_NOTIFICACIONES_MSJ_ID = "mensaje_id";
	public static String TABLE_NOTIFICACIONES_MSJ = "mensaje";
	public static String TABLE_NOTIFICACIONES_FECHA = "fecha";
	public static String TABLE_NOTIFICACIONES_LEIDO = "leido";
	private static String TABLE_NOTIFICACIONES_CREATE = "CREATE TABLE " + TABLE_NOTIFICACIONES + " (" +
																		  TABLE_NOTIFICACIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //0
																          TABLE_NOTIFICACIONES_TOPIC + " TEXT," +							//1
																		  TABLE_NOTIFICACIONES_MSJ_ID + " TEXT," +							//2
																		  TABLE_NOTIFICACIONES_MSJ + " TEXT," +								//3
																          TABLE_NOTIFICACIONES_FECHA + " NUMERIC," +						//4
																		  TABLE_NOTIFICACIONES_LEIDO + " INTEGER DEFAULT 0);";				//5

	public static String TABLE_SUSCRIPCIONES = "suscripciones";
	public static String TABLE_SUSCRIPCIONES_ID = "_id";
	public static String TABLE_SUSCRIPCIONES_TOPIC = "topic";
	private static String TABLE_SUSCRIPCIONES_CREATE = "CREATE TABLE " + TABLE_SUSCRIPCIONES + " (" +
																	     TABLE_SUSCRIPCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
																	     TABLE_SUSCRIPCIONES_TOPIC + " TEXT);";

	public static String TABLE_CORRECCIONES = "correccciones";
	public static String TABLE_CORRECCIONES_ID = "_id";
	public static String TABLE_CORRECCIONES_REL_ORIGIN = "relevamiento_origen";
	public static String TABLE_CORRECCIONES_REL_FINAL = "relevamiento_final";
	public static String TABLE_CORRECCIONES_SYNC = "sync";
	private static String TABLE_CORRECCIONES_SYNC_CREATE = "CREATE TABLE " + TABLE_CORRECCIONES + " (" +
			 															     TABLE_CORRECCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
																		     TABLE_CORRECCIONES_REL_ORIGIN + " TEXT," +
																			 TABLE_CORRECCIONES_REL_FINAL + " TEXT," +
																			 TABLE_CORRECCIONES_SYNC + " INTEGER DEFAULT 0);";

	public static String TRIGGER_RESPUESTAS = "trigger_respuestas";
	private static String TRIGGER_RESPUESTAS_CREATE = "CREATE TRIGGER "+TRIGGER_RESPUESTAS+
												    " BEFORE DELETE ON "+TABLE_VISITA+" FOR EACH ROW BEGIN " +
													"DELETE FROM "+TABLE_RESPUESTAS
												  +" WHERE "+TABLE_RESPUESTAS+"."+TABLE_RESPUESTAS_ID_VIST+"="+"OLD."+TABLE_VISITA_ID+";"+
			                                       " END; ";


	public static String VIEW_VISITAS_PENDIENTES_ARR ="visitas_pendientes_arr";
	private static String VIEW_VISITAS_PENDIENTES_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_VISITAS_PENDIENTES_ARR+" AS " +
															  "SELECT b." + TABLE_BENEFICIARIO_DOCUMENTO+","+//0
																	 "b." + TABLE_BENEFICIARIO_NOMBRE+","+   //1
																	 "b." + TABLE_BENEFICIARIO_APELLIDO+","+ //2
																	 "v." + TABLE_VISITA_LONG+","+			 //3
																	 "v." + TABLE_VISITA_LAT+","+			 //4
																	 "v." + TABLE_VISITA_PRES+","+			 //5
																 	 "v." + TABLE_VISITA_HORAINICIO+","+	 //6
																	 "v." + TABLE_VISITA_HORAFIN+","+		 //7
			 														 "v." + TABLE_VISITA_TIME+","+			 //8
																	 "v." + TABLE_VISITA_OBS+","+			 //9
																	 "v." + TABLE_VISITA_CODMOTIVO+","+      //10
																	 "v." + TABLE_VISITA_PROYECTO+","+		 //11
																	 "v." + TABLE_VISITA_ID+","+		     //12
																	 "v." + TABLE_VISITA_USER+","+		     //13
																	 "v." + TABLE_VISITA_DIST+","+ 			 //14
																	 "v." + TABLE_VISITA_DPTO+","+ 	 		 //15
																	 "b." + TABLE_BENEFICIARIO_TIPO+","+ 	 //16
															         "b." + TABLE_BENEFICIARIO_JEFE+","+ 	 //17
																	 "v." + TABLE_VISITA_LOC+","+ 			 //18
																	 "v." + TABLE_VISITA_ID_KEY + "," + 	 //19
																   	 "v." + TABLE_VISITA_ORIGINAL + " " + 	 //20
															  "FROM " + TABLE_VISITA + " v," +TABLE_BENEFICIARIO+" b "+
															  "WHERE v."+TABLE_VISITA_DOCBENEFICIARIO+"=b."+TABLE_BENEFICIARIO_DOCUMENTO+" AND " +
															  "v."+TABLE_VISITA_ENV+"=0 " + " AND " + "v."+TABLE_VISITA_EST+"=1 ";

	public static String VIEW_CAPTURAS_PENDIENTES_ARR ="capturas_pendientes_arr";
	private static String VIEW_CAPTURAS_PENDIENTES_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_CAPTURAS_PENDIENTES_ARR+" AS " +
														 	   "SELECT " + TABLE_CAPTURAS_ID+"," +	  			 //0
																		   TABLE_CAPTURAS_DOCBENEFICIARIO+","+   //1
																		   TABLE_CAPTURAS_PATH+"," +             //2
																		   TABLE_CAPTURAS_FECHA+","+  			 //3
																		   TABLE_CAPTURAS_NUMERO+","+  			 //4
                                                                           TABLE_CAPTURAS_ORIGEN + "," +         //5
																		   TABLE_CAPTURAS_HASH + " " +           //6
															   "FROM " + TABLE_CAPTURAS+" "+
															   "WHERE " + TABLE_CAPTURAS_EST+"=0 AND "+TABLE_CAPTURAS_IDVISITA+">0 ";

	public static String VIEW_ADJUNTOS_PENDIENTES_ARR ="adjuntos_pendientes_arr";
	private static String VIEW_ADJUNTOS_PENDIENTES_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_ADJUNTOS_PENDIENTES_ARR+" AS " +
															    "SELECT a."+TABLE_ADJ_ID+"," +	  //0
																	   "a."+TABLE_ADJ_PATH+","+   //1
																	   "a."+TABLE_ADJ_FECHA+","+  //2
																	   "v."+TABLE_VISITA_DOCBENEFICIARIO+" "+//3
															   "FROM " + TABLE_VISITA + " v," +TABLE_ADJ+" a "+
															   "WHERE " +"v."+ TABLE_VISITA_ID+"=a."+TABLE_ADJ_IDVISITA+
																		" AND a."+TABLE_ADJ_EST+"=0 ";

	public static String VIEW_UBICACIONES_PENDIENTES_ARR ="ubicaciones_pendientes_arr";
	private static String VIEW_UBICACIONES_PENDIENTES_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_UBICACIONES_PENDIENTES_ARR+" AS " +
																   "SELECT " + TABLE_UBICACIONES_ID+"," +			//0
																			   TABLE_UBICACIONES_LONG+","+			//1
																			   TABLE_UBICACIONES_LAT+","+			//2
																			   TABLE_UBICACIONES_ALTITUD+","+		//3
																			   TABLE_UBICACIONES_PRECS+","+			//4
																			   TABLE_UBICACIONES_PROVEDOR+","+		//5
																			   TABLE_UBICACIONES_HORA_OBT+","+		//6
																			   TABLE_UBICACIONES_HORA_GUARD+","+	//7
																			   TABLE_UBICACIONES_BATERIA+","+		//8
																			   TABLE_UBICACIONES_TIPO+","+			//9
																		       TABLE_UBICACIONES_USER+" "+			//10
																   "FROM " + TABLE_UBICACIONES+" "+
																   "WHERE " + TABLE_UBICACIONES_EST+"=0 ";

	public static String VIEW_OPERADORA_PENDIENTES_ARR ="operadora_pendientes_arr";
	private static String VIEW_OPERADORA_PENDIENTES_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_OPERADORA_PENDIENTES_ARR+" AS " +
																"SELECT " + TABLE_OPERADORA_ID+"," +					//0
																			TABLE_OPERADORA_CODOPERADORA+","+			//1
																			TABLE_OPERADORA_SERIAL_SIM+","+			    //2
																			TABLE_OPERADORA_COUNTRYISO+","+			    //3
																			TABLE_OPERADORA_NOM+","+					//4
																			TABLE_OPERADORA_FECHA+","+					//5
																		    TABLE_OPERADORA_USUARIO+" "+				//6
															    "FROM " + TABLE_OPERADORA+" "+
															    "WHERE " + TABLE_OPERADORA_ESTADO+"=0 ";

	public static String VIEW_PROYECTOS ="view_proyectos";
	private static String VIEW_PROYECTOS_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_PROYECTOS+" AS " +
												 "SELECT p." + TABLE_PROYECTOS_COD+","+     	       //0
														"p." + TABLE_PROYECTOS_NOM+","+     	      //1
														"i." + TABLE_INSTITUCIONES_NOM+","+ 	      //2
														"p." + TABLE_PROYECTOS_TIPO+","+    	      //3
														"p." + TABLE_PROYECTOS_CANT_MIN_IMG+","+      //4
													    "p." + TABLE_PROYECTOS_ALTA_DESTINATARIO+","+ //5
														"p." + TABLE_PROYECTOS_ENTIDAD_RELEVAR + " "+ //6
												 "FROM " + TABLE_PROYECTOS + " p," +TABLE_INSTITUCIONES+" i "+
												 "WHERE p."+TABLE_PROYECTOS_INST+"=i."+TABLE_INSTITUCIONES_COD;

	public static String VIEW_CAPTURAS_EVENTO_ARR ="capturas_eventos_arr";
	private static String VIEW_CAPTURAS_EVENTO_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_CAPTURAS_EVENTO_ARR+" AS " +
															 "SELECT ce." + TABLE_CAPTURA_EVENTOS_ID+"," +			//0
																	 "e." + TABLE_EVENTOS_USER+","+   				//1
																	"ce." + TABLE_CAPTURA_EVENTOS_PATH+"," +        //2
																	"ce." + TABLE_CAPTURA_EVENTOS_FECHA+","+  	    //3
																	"ce." + TABLE_CAPTURA_EVENTOS_HASH+" "+  	    //4
															"FROM " + TABLE_CAPTURA_EVENTOS+" ce,"+TABLE_EVENTOS+" e "+
															"WHERE ce." + TABLE_CAPTURA_EVENTOS_envio+"=0 AND " +
															      "ce."+TABLE_CAPTURA_EVENTOS_IDEVENTO+"=e."+TABLE_EVENTOS_ID;

	public static String VIEW_REPORTE_RELEVADO ="reporte_relevado_arr";
	private static String VIEW_REPORTE_RELEVADO_ARR_CREATE = "CREATE VIEW IF NOT EXISTS "+VIEW_REPORTE_RELEVADO+" AS " +
															  "SELECT b."+TABLE_BENEFICIARIO_NOMBRE+"||' '||b."+TABLE_BENEFICIARIO_APELLIDO+","+				//0
																     "b."+TABLE_BENEFICIARIO_DOCUMENTO+","+														//1
																	 "v."+TABLE_VISITA_HORAINICIO+","+															//2
			                                                         "m."+TABLE_MOTIVOS_CODIGO+","+																//3
																	 "m."+TABLE_MOTIVOS_DESCRIPCION+","+														//4
																	 "v."+TABLE_VISITA_LONG+","+																//5
																	 "v."+TABLE_VISITA_LAT+","+																	//6
																	 "b."+TABLE_BENEFICIARIO_EST+","+															//7
																	 "v."+TABLE_VISITA_EST+","+																	//8
																     "v."+TABLE_VISITA_ENV+","+																	//9
																	 "v."+TABLE_VISITA_ID+"," +																	//10
																	 "b."+TABLE_BENEFICIARIO_NOMBRE_VERIF+"||' '||b."+TABLE_BENEFICIARIO_APELLIDO_VERIF+","+	//11
															  		 "v." + TABLE_VISITA_OBS + "," +															//12
																	 "v." + TABLE_VISITA_ID_KEY + " " +														    //13
															  "FROM " + TABLE_VISITA + " v," + TABLE_BENEFICIARIO + " b," + TABLE_MOTIVOS +" m "+
															  "WHERE v."+TABLE_VISITA_DOCBENEFICIARIO+"=b."+ TABLE_BENEFICIARIO_DOCUMENTO + " AND " +
																    "m."+TABLE_MOTIVOS_CODIGO+"=v."+TABLE_VISITA_CODMOTIVO+
																	" AND v."+TABLE_VISITA_CORRECTO + "=1";


	public DataBaseMaestro(Context contexto,String bdnombre,CursorFactory factory,int version) {
		super(contexto, bdnombre, null, version);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_BENEFICIARIO_CREATE);
		db.execSQL(TABLE_VISITA_CREATE);
		db.execSQL(TABLE_INSTITUCIONES_CREATE);
		db.execSQL(TABLE_PROYECTOS_CREATE);
		db.execSQL(TABLE_MOTIVOS_CREATE);
		db.execSQL(TABLE_SECCIONES_CREATE);
		db.execSQL(TABLE_CAPTURAS_CREATE_1);
		db.execSQL(TABLE_ADJ_CREATE);
		db.execSQL(TABLE_PREGUNTAS_CREATE);
		db.execSQL(TABLE_POSRESPUESTAS_CREATE);
		db.execSQL(TABLE_RESPUESTAS_CREATE);
		db.execSQL(TABLE_UBICACIONES_CREATE);
		db.execSQL(TABLE_OPERADORA_CREATE);
		db.execSQL(TABLE_EVENTOS_CREATE);
		db.execSQL(TABLE_CAPTURA_EVENTOS_CREATE);
		db.execSQL(TABLE_DEPARTAMENTOS_CREATE);
		db.execSQL(TABLE_DISTRITOS_CREATE);
		db.execSQL(TABLE_RECORRIDO_CREATE);
		db.execSQL(TABLE_EVTCELL_CREATE);
		db.execSQL(TABLE_NOTIFICACIONES_CREATE);
		db.execSQL(TABLE_SUSCRIPCIONES_CREATE);
		db.execSQL(TABLE_PREGUNTAS_CONDICION_CREATE);
		db.execSQL(TABLE_SECCION_CONDICION_CREATE);
		db.execSQL(TABLE_CORRECCIONES_SYNC_CREATE);

		db.execSQL(TRIGGER_RESPUESTAS_CREATE);

		db.execSQL(VIEW_VISITAS_PENDIENTES_ARR_CREATE);
		db.execSQL(VIEW_CAPTURAS_PENDIENTES_ARR_CREATE);
		db.execSQL(VIEW_ADJUNTOS_PENDIENTES_ARR_CREATE);
		db.execSQL(VIEW_UBICACIONES_PENDIENTES_ARR_CREATE);
		db.execSQL(VIEW_OPERADORA_PENDIENTES_ARR_CREATE);
		db.execSQL(VIEW_PROYECTOS_CREATE);
		db.execSQL(VIEW_CAPTURAS_EVENTO_ARR_CREATE);
		db.execSQL(VIEW_REPORTE_RELEVADO_ARR_CREATE);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion<4){
			db.execSQL("ALTER TABLE " + TABLE_BENEFICIARIO + " ADD COLUMN " + TABLE_BENEFICIARIO_FAMILY + " INTEGER DEFAULT 0");
		}
		if(oldVersion<6){
			db.execSQL("ALTER TABLE " + TABLE_PROYECTOS + " ADD COLUMN " + TABLE_PROYECTOS_TIPO + " INTEGER DEFAULT 0");
			//db.execSQL("DROP VIEW IF EXISTS " + VIEW_PROYECTOS);
			//db.execSQL(VIEW_PROYECTOS_CREATE);
		}
		if(oldVersion<7){
			db.execSQL(TABLE_SECCION_CONDICION_CREATE);
		}

		if(oldVersion<8){
			db.execSQL("ALTER TABLE " + TABLE_PROYECTOS + " ADD COLUMN " + TABLE_PROYECTOS_CANT_MIN_IMG + " INTEGER DEFAULT 1");
			db.execSQL("ALTER TABLE " + TABLE_PROYECTOS + " ADD COLUMN " + TABLE_PROYECTOS_ALTA_DESTINATARIO + " INTEGER DEFAULT 1");
			//db.execSQL("DROP VIEW IF EXISTS " + VIEW_PROYECTOS);
			//db.execSQL(VIEW_PROYECTOS_CREATE);
		}

		if(oldVersion<9){
			try{
				db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_DPTO + " INTEGER DEFAULT 0");
			}catch (Exception ex){}

			try{
				db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_DIST + " INTEGER DEFAULT 0");
			}catch (Exception ex){}

			try{
				db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_LOC + " INTEGER DEFAULT 0");
			}catch (Exception ex){}

			//db.execSQL("DROP VIEW IF EXISTS " + VIEW_VISITAS_PENDIENTES_ARR);
			//db.execSQL(VIEW_VISITAS_PENDIENTES_ARR_CREATE);
		}

		if(oldVersion<10){
			db.execSQL("ALTER TABLE " + TABLE_RESPUESTAS + " ADD COLUMN " + TABLE_RESPUESTAS_NRO_SUBFORM + " INTEGER DEFAULT 0");
		}

		if(oldVersion<11){
			db.execSQL("ALTER TABLE " + TABLE_BENEFICIARIO + " ADD COLUMN " + TABLE_BENEFICIARIO_REGISTRO_ID + " INTEGER DEFAULT 0");
			//db.execSQL("DROP VIEW IF EXISTS " + VIEW_VISITAS_PENDIENTES_ARR);
			//db.execSQL(VIEW_VISITAS_PENDIENTES_ARR_CREATE);
			//db.execSQL("DROP VIEW IF EXISTS " + VIEW_REPORTE_RELEVADO);
			//db.execSQL(VIEW_REPORTE_RELEVADO_ARR_CREATE);
			db.execSQL("ALTER TABLE " + TABLE_SECCIONES + " ADD COLUMN " + TABLE_SECCIONES_MULTIMEDIA + " TEXT");
		}

		if(oldVersion<12){
			db.execSQL("DROP VIEW IF EXISTS " + VIEW_REPORTE_RELEVADO);
			db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_CORRECTO + " INTEGER DEFAULT 1");
			db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_ID_KEY + " TEXT DEFAULT ''");
			db.execSQL("ALTER TABLE " + TABLE_VISITA + " ADD COLUMN " + TABLE_VISITA_ORIGINAL + " INTEGER DEFAULT 1");
			db.execSQL(VIEW_REPORTE_RELEVADO_ARR_CREATE);
			db.execSQL(TABLE_CORRECCIONES_SYNC_CREATE);
			db.execSQL("DROP VIEW IF EXISTS " + VIEW_VISITAS_PENDIENTES_ARR);
			db.execSQL(VIEW_VISITAS_PENDIENTES_ARR_CREATE);
			db.execSQL(TABLE_CAPTURAS_CREATE_2);
			db.execSQL("INSERT INTO " + TABLE_CAPTURAS2 + " SELECT * FROM " + TABLE_CAPTURAS);
			db.execSQL("DROP TABLE " + TABLE_CAPTURAS);
			db.execSQL("ALTER TABLE " + TABLE_CAPTURAS2 + " RENAME TO " + TABLE_CAPTURAS + ";");
			db.execSQL("ALTER TABLE " + TABLE_PROYECTOS + " ADD COLUMN " + TABLE_PROYECTOS_ENTIDAD_RELEVAR + " TEXT");
			db.execSQL("DROP VIEW IF EXISTS " + VIEW_PROYECTOS);
			db.execSQL(VIEW_PROYECTOS_CREATE);
		}

		if(oldVersion<13){
			try{
				db.execSQL("ALTER TABLE " + TABLE_CAPTURAS + " ADD COLUMN " + TABLE_CAPTURAS_ORIGEN + " INTEGER DEFAULT 0");
			}catch (SQLiteException ex){}
			try{
				db.execSQL("ALTER TABLE " + TABLE_CAPTURAS + " ADD COLUMN " + TABLE_CAPTURAS_HASH + " TEXT DEFAULT ''");
			}catch (SQLiteException ex){}
			db.execSQL("DROP VIEW IF EXISTS " + VIEW_CAPTURAS_PENDIENTES_ARR);
			db.execSQL(VIEW_CAPTURAS_PENDIENTES_ARR_CREATE);
		}

		db.execSQL("ALTER TABLE " + TABLE_CAPTURAS + " ADD COLUMN " + TABLE_CAPTURAS_NRO_ENVIOS + " INTEGER DEFAULT 0");
		db.execSQL("ALTER TABLE " + TABLE_CAPTURAS + " ADD COLUMN " + TABLE_CAPTURAS_SYNC + " INTEGER DEFAULT 0");
		db.execSQL("ALTER TABLE " + TABLE_CAPTURA_EVENTOS + " ADD COLUMN " + TABLE_CAPTURA_EVENTOS_NRO_ENVIOS + " INTEGER DEFAULT 0");
		db.execSQL("ALTER TABLE " + TABLE_CAPTURA_EVENTOS + " ADD COLUMN " + TABLE_CAPTURA_EVENTOS_HASH + " TEXT DEFAULT ''");
		db.execSQL("ALTER TABLE " + TABLE_CAPTURA_EVENTOS + " ADD COLUMN " + TABLE_CAPTURA_EVENTOS_SYNC + " INTEGER DEFAULT 0");
		db.execSQL("DROP VIEW IF EXISTS " + VIEW_CAPTURAS_EVENTO_ARR);
		db.execSQL(VIEW_CAPTURAS_EVENTO_ARR_CREATE);
	}
}