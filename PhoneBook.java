import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Program: Obs³uga ksi¹¿ki telefonicznej
 *    Plik: PhoneBook.java
 *          
 *   Autor: Damian Bednarz 241283
 *    Data: styczeñ 2019 r.
 *	
 */

class PhoneBookException extends Exception {

	private static final long serialVersionUID = 1L;

	public PhoneBookException (String message) {
		super(message);
	}
}

public class PhoneBook {

	private ConcurrentHashMap <String,String> list;
	
	PhoneBook (){
		list = new ConcurrentHashMap <>();
	}
	
	@SuppressWarnings("unchecked")
	String load (String file_name) throws PhoneBookException {
		try(ObjectInputStream input =new ObjectInputStream(new FileInputStream(new File(file_name)))){
			list = (ConcurrentHashMap<String,String>)input.readObject();
			return"OK";
		}catch(ClassNotFoundException e){
			throw new PhoneBookException ("ERROR b³¹d odczytu z pliku");
		}catch (FileNotFoundException e) {
			throw new PhoneBookException ("ERROR nie znaleziono pliku");
		}catch(IOException e) {
			throw new PhoneBookException ("ERROR b³¹d odczytu z pliku");
		}
	}
	
	String save (String file_name) throws PhoneBookException {
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file_name))){
			output.writeObject(list);
			return "OK";
		}catch(IOException e ) {
			throw new PhoneBookException ("ERROR b³ad zapisu do pliku");
		}
	}
	
	String get (String name) {
		if(list.get(name)!=null) {
			return "OK" + list.get(name);
		}else {
			return "ERROR nie ma osoby o takiej nazwie";
		}
	}
	
	String put (String name, String number) throws PhoneBookException {
		if(!list.containsKey(name)) {
			list.put(name, number);
			return "OK";
		}
		throw new PhoneBookException ("Ta osoba ju¿ jest na liœcie");
	}
	
	String replace (String name, String number) throws PhoneBookException {
		if(list.containsKey(name)) {
			list.put(name, number);
			return name;
		}
		throw new PhoneBookException ("ERROR nie ma takiej osoby");
	}
	
	String delete (String name) throws PhoneBookException {
		if(list.remove(name)!=null) {
			return "OK";
		}
		throw new PhoneBookException ("ERROR nie ma takiej osoby");
	}
	
	String list () {
		String names = list.keySet().toString();
		return names.substring(1,names.length()-1).replace(" , ", "\n");
	}
}
