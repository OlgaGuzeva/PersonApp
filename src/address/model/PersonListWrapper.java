package address.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Вспомогательный класс для обёртывания списка адресатов.
 * Используется для сохранения списка адресатов в XML.
 * 
 * @author Marco Jakob
 */
@XmlRootElement(name = "persons")
public class PersonListWrapper {
	
	private List persons;
	
	@XmlElement(name = "person")
	public List getPersons(){
		return persons;
	}
	
	public void setPersons(List persons){
		this.persons = persons;
	}

}