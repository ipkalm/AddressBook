package main.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by astronaut on 06.09.15.
 * Helper class to wrap a list of persons. This is used
 * for saving the list if person to XML.
 *
 * @author astronaut
 */
@XmlRootElement(name = "persons")
public class PersonListWrapper {
    private List<Person> persons;

    @XmlElement(name = "person")
    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
