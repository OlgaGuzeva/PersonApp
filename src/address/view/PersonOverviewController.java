package address.view;

import address.MainApp;
import address.model.Person;
import address.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PersonOverviewController {
	@FXML
	private TableView<Person> personTable;
	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> lastNameColumn;
	
	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label streetLabel;
	@FXML
	private Label postalCodeLabel;
	@FXML
	private Label cityLabel;
	@FXML
	private Label birthdayLabel;
	
	private MainApp mainApp;
	
	/**
     * Конструктор.
     * Конструктор вызывается раньше метода initialize().
     */
	public PersonOverviewController(){
		
	}
	
	/**
     * Инициализация класса-контроллера. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
	@FXML
	private void initialize(){
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
		
		showPersonDetails(null);
		
		personTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showPersonDetails(newValue));
	}
	
	/**
     * Вызывается главным приложением, которое даёт на себя ссылку.
     * 
     * @param mainApp
     */
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
		
		personTable.setItems(mainApp.getPersonData());
	}
	
	/**
	 * Заполняет все текстовые поля, отображая подробности об адресате.
	 * Если указанный адресат = null, то все текстовые поля очищаются.
	 * 
	 * @param person — адресат типа Person или null
	 */
	private void showPersonDetails(Person person){
		if(person != null){
			firstNameLabel.setText(person.getFirstName());
			lastNameLabel.setText(person.getLastName());
			streetLabel.setText(person.getStreet());
			postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
			cityLabel.setText(person.getCity());
			birthdayLabel.setText(DateUtil.format(person.getBirthday()));
		}else{
			firstNameLabel.setText("");
			lastNameLabel.setText("");
			streetLabel.setText("");
			postalCodeLabel.setText("");
			cityLabel.setText("");
			birthdayLabel.setText("");
		}
	}
	
	/**
	 * Вызывается, когда пользователь кликает по кнопке удаления.
	 */
	@FXML
	private void HandleDeletePerson(){
		int selectedIndex = personTable.getSelectionModel().getFocusedIndex();
		if(selectedIndex >= 0){
			personTable.getItems().remove(selectedIndex);
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Person Selected");
			alert.setContentText("Please select a person in the table.");
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Вызывается, когда пользователь кликает по кнопке New...
	 * Открывает диалоговое окно с дополнительной информацией нового адресата.
	 */
	@FXML
	private void handleNewPerson(){
		Person tempPerson = new Person();
		boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
		if(okClicked){
			mainApp.getPersonData().add(tempPerson);
		}
	}
	
	/**
	 * Вызывается, когда пользователь кликает по кнопка Edit...
	 * Открывает диалоговое окно для изменения выбранного адресата.
	 */
	@FXML
	private void handleEditPerson(){
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if(selectedPerson != null){
			boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
			if(okClicked){
				showPersonDetails(selectedPerson);
			}
		}else{
			//Ничего не выбрано
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection!");
			alert.setHeaderText("No person selected");
			alert.setContentText("Please, select a person in the table");
			
			alert.showAndWait();
		}
	}
	
}
