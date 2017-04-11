package address;

import java.io.File;
import java.io.IOException;
import java.security.SecurityPermission;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import address.model.Person;
import address.model.PersonListWrapper;
import address.view.PersonEditDialogController;
import address.view.PersonOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	/**
     * Данные, в виде наблюдаемого списка адресатов.
     */
	private ObservableList<Person> personData = FXCollections.observableArrayList();
	
	/**
     * Конструктор
     */
	public MainApp(){
		// В качестве образца добавляем некоторые данные
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
	}
	
	/**
     * Возвращает данные в виде наблюдаемого списка адресатов.
     * @return
     */
	public ObservableList<Person> getPersonData(){
		return personData;
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");
		
		//устанавливаем иконку для приложения
		this.primaryStage.getIcons().add(new Image("file:resources/images/Death_Note.png"));
		
		initRootLayout();
		
		showPersonOverview();		
	}
	
	/*������������� ��������� ������*/
	public void initRootLayout(){
		try{
			//��������� �������� ����� �� fxml 
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			//���������� �����, ���������� �������� �����
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Показывает в корневом макете сведения об адресатах.
	 */
	public void showPersonOverview(){
		try{
			// Загружаем сведения об адресатах.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();
			
			// Помещаем сведения об адресатах в центр корневого макета.
			rootLayout.setCenter(personOverview);
			
			// Даём контроллеру доступ к главному приложению.
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Открывает диалоговое окно для изменения деталей указанного адресата.
	 * Если пользователь кликнул OK, то изменения сохраняются в предоставленном
	 * объекте адресата и возвращается значение true.
	 * 
	 * @param person - объект адресата, который надо изменить
	 * @return true, если пользователь кликнул OK, в противном случае false.
	 */
	public boolean showPersonEditDialog(Person person){
		try{
			//Загружаем fxml-файл и создаем новую сцену
			//для всплывающего диалогового окна
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			//Создаем диалоговое окно Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			//Передаем адресата в контроллер.
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);
			
			//Отображаем диалоговое окно и ждем, пока пользователь его не закроет
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Stage getPrimaryStage(){
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Возвращает preference файла адресатов, то есть, последний открытый файл.
	 * Этот preference считывается из реестра, специфичного для конкретной
	 * операционной системы. Если preference не был найден, то возвращается null.
	 * 
	 * @return
	 */
	public File getPersonFilePath(){
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if(filePath != null){
			return new File(filePath);
		}else{
			return null;
		}
	}
	
	/**
	 * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
	 * в реестре, специфичном для конкретной операционной системы.
	 * 
	 * @param file - файл или null, чтобы удалить путь
	 */
	public void setPersonFilePath(File file){
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if(file != null){
			prefs.put("filePath", file.getPath());
			
			//Обновление заглавия сцены
			primaryStage.setTitle("AddressApp - " + file.getName());
		}else{
			prefs.remove("filePath");
			
			//Обновление заглавия сцены
			primaryStage.setTitle("AddressApp");
		}
	}
	
	/**
	 * Загружает информацию об адресатах из указанного файла.
	 * Текущая информация об адресатах будет заменена.
	 * 
	 * @param file
	 */
	public void loadPersonDataFromFile(File file){
		try{
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			//Чтение XML из файла и демаршализация
			PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);
			
			personData.clear();
			personData.addAll(wrapper.getPersons());
			
			//Сохраняем путь к файлу в реестре
			setPersonFilePath(file);
		}catch(Exception e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Сохраняет текущую информацию об адресатах в указанном файле.
	 * 
	 * @param file
	 */
	public void saxePersonDataToFile(File file){
		try{
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			//Обертываем наши данные об адресатах
			PersonListWrapper wrapper = new PersonListWrapper();
			wrapper.setPersons(personData);
			
			//Маршаллируем и сохраняем XML файл
			m.marshal(wrapper, file);
			
			//Сохраняем путь к файлу
			setPersonFilePath(file);
		}catch(Exception e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());
			
			alert.showAndWait();
		}
	}
	
	
}
