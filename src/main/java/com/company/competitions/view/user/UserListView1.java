package com.company.competitions.view.user;

import com.company.competitions.entity.Department;
import com.company.competitions.entity.Position;
import com.company.competitions.entity.User;
import com.company.competitions.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.upload.FileUploadField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Route(value = "users1", layout = MainView.class)
@ViewController(id = "User.list1")
@ViewDescriptor(path = "user-list-view1.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView1 extends StandardListView<User> {
    @ViewComponent
    private FileUploadField uploadField;

    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;

    @Subscribe(id = "loadButton", subject = "clickListener")
    public void onLoadButtonClick(final ClickEvent<JmixButton> event) {

        byte[] fileContent = uploadField.getValue();
        if (fileContent == null || fileContent.length == 0) {
            notifications.show("Файл не выбран");
            return;
        }
        try {
            processExcelFile(new ByteArrayInputStream(fileContent));
            notifications.show("Данные успешно загружены");
        } catch (Exception e) {
            notifications.show("Ошибка при обработке файла: " + e.getMessage());
        }
    }
    Integer i = 0;
    private void processExcelFile(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);



        List<User> users = dataManager.load(User.class).query("select e from User e where e.username <> 'admin'").list();
        for (User user : users) {
            dataManager.remove(user);
        }
        List<Department> deps = dataManager.load(Department.class).all().list();
        for (Department dep : deps) {
            dataManager.remove(dep);
        }
        List<Position> positions = dataManager.load(Position.class).all().list();
        for (Position p : positions) {
            dataManager.remove(p);
        }
        Sheet sheet0 = workbook.getSheetAt(0);
        for (Row row : sheet0) {


            if (row.getRowNum() == 0) continue; // пропускаем заголовок

            String numDep = getCellString(row.getCell(0));
            String nameDep = getCellString(row.getCell(1));
            Department dep = dataManager.create(Department.class);
            dep.setNum(numDep);
            dep.setName(nameDep);





            dataManager.save(dep);
        }
        Sheet sheet1 = workbook.getSheetAt(1);
        for (Row row : sheet1) {


            if (row.getRowNum() == 0) continue; // пропускаем заголовок

            String shortName = getCellString(row.getCell(0));
            String longName = getCellString(row.getCell(1));
            Position poss = dataManager.create(Position.class);
            poss.setShortName(shortName);
            poss.setLongName(longName);






            dataManager.save(poss);
        }




        Sheet sheet2 = workbook.getSheetAt(3);

        for (Row row : sheet2) {

            i++;
            if (row.getRowNum() == 0) continue; // пропускаем заголовок

            String persNum = getCellString(row.getCell(0));
            String lastName = getCellString(row.getCell(1));
            String username = getCellString(row.getCell(2));
            String otcheName = getCellString(row.getCell(3));
            String birthDate = getCellString(row.getCell(4));
            //System.out.println(birthDate);
            String pol = getCellString(row.getCell(5));
            String login = getCellString(row.getCell(6));
            String department = getCellString(row.getCell(7));
            String position = getCellString(row.getCell(8));
            String persNumRuk = getCellString(row.getCell(9));



               User user = dataManager.create(User.class);
                user.setFirstName(username);



            user.setPersNumber(persNum);
            user.setLastName(lastName);
            user.setFirstName(username);
            user.setOtchestvo(otcheName);
            user.setBirthDate(birthDate);
            user.setPol(pol);
            user.setUsername(login);
            Department dep = dataManager.load(Department.class)
                    .query("select e from Department e where e.num =:num").parameter("num", department).one();
            user.setDepartment(dep);

            Position pos = dataManager.load(Position.class)
                    .query("select e from Position_ e where e.shortName =:pos").parameter("pos", position).one();
            user.setPosition(pos);
            user.setPersNumberRuk(persNumRuk);

            dataManager.save(user);
        }

        Sheet sheet3 = workbook.getSheetAt(2);

        for (Row row : sheet3) {

            i++;
            if (row.getRowNum() == 0) continue; // пропускаем заголовок

            String persNum = getCellString(row.getCell(0));
            String lastName = getCellString(row.getCell(1));
            String username = getCellString(row.getCell(2));
            String otcheName = getCellString(row.getCell(3));
            String birthDate = getCellString(row.getCell(4));
            //System.out.println(birthDate);
            String pol = getCellString(row.getCell(5));
            String login = getCellString(row.getCell(6));
            String department = getCellString(row.getCell(7));
            String position = getCellString(row.getCell(8));




            User user = dataManager.create(User.class);
            user.setFirstName(username);
            user.setPersNumber(persNum);
            user.setLastName(lastName);
            user.setFirstName(username);
            user.setOtchestvo(otcheName);
            user.setBirthDate(birthDate);
            user.setPol(pol);
            user.setUsername(login);
            Department dep = dataManager.load(Department.class)
                    .query("select e from Department e where e.num =:num").parameter("num",department).one();
            user.setDepartment(dep);

            Position pos = dataManager.load(Position.class)
                    .query("select e from Position_ e where e.shortName =:pos").parameter("pos", position).one();
            user.setPosition(pos);
            user.setPersNumberRuk(null);

            dataManager.save(user);
        }


        System.out.println(i);
        workbook.close();
    }


    private String getCellString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
            case FORMULA:
                return formatter.formatCellValue(cell);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}








