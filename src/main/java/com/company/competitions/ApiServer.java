package com.company.competitions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.String;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.company.competitions.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.ReferenceToEntitySupport;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.settings.UserSettingsService;
import io.jmix.flowui.xml.layout.loader.component.usermenu.UserMenuItemLoader;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rest/service")
public class ApiServer {


    @GetMapping("/sample")
    public String sample() {
        return "Sample";
    }

    private final CurrentAuthentication currentAuthentication;

    public ApiServer(CurrentAuthentication currentAuthentication, DataManager dataManager, FileStorage fileStorage) {
        this.currentAuthentication = currentAuthentication;
        this.dataManager = dataManager;
        this.fileStorage = fileStorage;


    }

    private final DataManager dataManager;

    private final FileStorage fileStorage;


    //при создании документа создается сущность документа и первая версия которая привязывается к документу
    @PostMapping("/upload")
    public Document upload(@RequestPart("file") MultipartFile file,
                           @RequestPart("title") String title) throws IOException {

        UUID currentUserId = ((User) currentAuthentication.getUser()).getId();
        System.out.println(" not easy");
        String filename = file.getOriginalFilename();
        FileRef fileRef = fileStorage.saveStream(filename, file.getInputStream());

        // Создаем новые сущности
        Document document = dataManager.create(Document.class);
        document.setName(title);

        //document.set(fileRef);
        User owner = dataManager.load(User.class).id(currentUserId).one();
        document.setOwner(owner);
        document.setStatus("Черновик");


        dataManager.save(document);
        DocumentVersion newVersionDoc = dataManager.create(DocumentVersion.class);
        newVersionDoc.setFileRef(fileRef);
        newVersionDoc.setNumVersion(1);
        newVersionDoc.setName(filename);
        newVersionDoc.setDocument(document);
        dataManager.save(newVersionDoc);

        System.out.println(document);
        // return new FileResponse(document.getFile().getPath(), filename);
        return document;
    }


    public static class DocumentInput {
        private UUID documentVersionUuid;
        private UUID documentId;
        private UUID soglasovanieId;


        public UUID getDocumentVersionUuid() {
            return documentVersionUuid;
        }

        public void setDocumentVersionUuid(UUID documentVersionUuid) {
            this.documentVersionUuid = documentVersionUuid;
        }

        public UUID getDocumentId() {
            return documentId;
        }

        public void setDocumentId(UUID documentId) {
            this.documentId = documentId;
        }

        public UUID getSoglasovanieId() {
            return soglasovanieId;
        }

        public void setSoglasovanieId(UUID soglasovanieId) {
            this.soglasovanieId = soglasovanieId;
        }

    }

    public static class DocInput {

        private UUID documentId;


        public UUID getDocumentId() {
            return documentId;
        }

        public void setDocumentId(UUID documentId) {
            this.documentId = documentId;
        }


    }
//отдаю все версии документа
@PostMapping("/allDocVersion")
public List<DocumentVersion> allDocVersion(@RequestParam UUID documentId){

    UUID documentUuid = documentId;

    Document document = dataManager.load(Document.class).id(documentUuid).one();

    List<DocumentVersion> userDocuments = dataManager.load(DocumentVersion.class)
            .query("select e from DocumentVersion e where e.document =:docId order by e.createdDate")
            .parameter("docId", document)
            .list();

    return userDocuments;
}
//список пользаков с ролями по отношению к версии
@GetMapping("/userDocRole")
public List<User> userDocRole(@RequestParam UUID docVersionId){

    DocumentVersion documentVersion = dataManager.load(DocumentVersion.class).id(docVersionId).one();
    DocRole roles = documentVersion.getDocRole();
    if (roles.getRedaktori() != null) {
        return roles.getRedaktori();
    }

    return null;
}
//ПУНКТ 3 - меняет роли для версии
@PostMapping("/changeDocRole")
public DocumentVersion changeDocRole(@RequestParam UUID docVersionId, @RequestParam UUID userId){

    DocumentVersion documentVersion = dataManager.load(DocumentVersion.class).id(docVersionId).one();
    DocRole roles = documentVersion.getDocRole();
    User red = dataManager.load(User.class).id(userId).one();
    roles.getRedaktori().add(red);
    documentVersion.setDocRole(roles);
    dataManager.save(documentVersion);

    return documentVersion;
}
    //это на создание версий уже существующего выбранного документа
    //мы корректируем версию документа и после сохранения создается новая версия
    @PostMapping("/uploadDocVersion")
    public Document uploadDocVersion(@RequestParam UUID documentId, @RequestPart("file") MultipartFile file) throws IOException {

        UUID documentUuid = documentId;
        //DocumentVersion oldVersion = dataManager.load(DocumentVersion.class).id(docVersionUuid).one();
        Document document = dataManager.load(Document.class).id(documentUuid).one();

        String filename = file.getOriginalFilename();
        FileRef fileRef = fileStorage.saveStream(filename, file.getInputStream());
        System.out.println(" not easy 3");

        List<DocumentVersion> userDocuments = dataManager.load(DocumentVersion.class)
                .query("select e from DocumentVersion e where e.document =:docId order by e.createdDate desc")
                .parameter("docId", document)
                .list();
// .parameter("user", ((User) currentAuthentication.getUser()))
        // Создаем новые сущности

        DocumentVersion newVersionDoc = dataManager.create(DocumentVersion.class);
        newVersionDoc.setFileRef(fileRef);
        //берем версию последнего созданного
        newVersionDoc.setNumVersion(userDocuments.get(0).getNumVersion() + 1);
        newVersionDoc.setName(filename);
        newVersionDoc.setDocument(document);
        dataManager.save(newVersionDoc);

        System.out.println(document);
        // return new FileResponse(document.getFile().getPath(), filename);
        return document;
    }

    @GetMapping("/allDocs")
    public List<Document> allDocs() {
        List<Document> userDocuments = dataManager.load(Document.class)
                .query("select e from Document e where e.owner =:user")
                .parameter("user", ((User) currentAuthentication.getUser())).list();
        System.out.println(userDocuments);
        return userDocuments;
    }

    //или переделать на idшник входной параметр?
    //когда в таблице документа выбираем документ, который уже есть - нужно отобразить его версии для дальнейшего открытия
    @PostMapping("/allVersionDocs")
    public List<DocumentVersion> allVersionDocs(@RequestBody DocInput input) {
        System.out.println(input.getDocumentId());
        List<DocumentVersion> userDocuments = dataManager.load(DocumentVersion.class)
                .query("select e from DocumentVersion e where e.document =:docId")
                .parameter("docId", dataManager.load(Document.class).id(input.getDocumentId()).one())
                .list();
        return userDocuments;
    }

    //СОГЛАСОВАНИЕ
    @PostMapping("/createSoglasovanie")
    public Soglasovanie createSoglasovanie(@RequestBody DocumentInput input) {
        Soglasovanie soglasovanie = dataManager.create(Soglasovanie.class);
        soglasovanie.setDocVersion(dataManager.load(DocumentVersion.class).id(input.getDocumentVersionUuid()).one());
        soglasovanie.setStatusSogl("На рассмотрении");
        dataManager.save(soglasovanie);
        return soglasovanie;
    }

    //отдать всех пользователей
    @GetMapping("/allUsers")
    public List<User> allUsers() {
        List<User> users = dataManager.load(User.class)
                .query("select e from User e where e.username <> 'admin' and e.username <> 'anonymous'")
                .list();

        return users;
    }
    @GetMapping("/docInfo")
    public Document docInfo(@RequestParam UUID documentId) {
        Document doc = dataManager.load(Document.class).id(documentId).one();

        return doc;
    }

    //создать пользователя-согласованта
    @PostMapping("/createUserSoglasovant")
    public UserSogl createUserSoglasovant(@RequestBody DocumentInput input) {
        UserSogl userSogl = dataManager.create(UserSogl.class);
        userSogl.setSoglasovanie(dataManager.load(Soglasovanie.class).id(input.getSoglasovanieId()).one());
        userSogl.setStatus("На рассмотрении");
        dataManager.save(userSogl);
        return userSogl;
    }

    //когда пользователь-согласовант нажимает согласовать
    @PostMapping("/toAgree")
    public UserSogl toAgree(@RequestParam UUID userSoglasovantId, @RequestParam UUID soglId) {
        UserSogl userSogl = dataManager.load(UserSogl.class).id(userSoglasovantId).one();
        Soglasovanie sogl = dataManager.load(Soglasovanie.class).id(soglId).one();
        userSogl.setStatus("Согласовано");
        String history = sogl.getHistory();
        history += userSogl.getUser().getLastName() + " " + userSogl.getUser().getLastName() + "согласовал";
        sogl.setHistory(history);
        //здесь надо пересчитать
        dataManager.save(userSogl);
        dataManager.save(sogl);
        return userSogl;
    }

    //когда пользователь согласовант хочет внести комментарий и это вносится в историю согласования
    @PostMapping("/setComment")
    public UserSogl setComment(@RequestParam UUID userSoglasovantId, @RequestParam UUID soglId, @RequestParam String comment) {
        UserSogl userSogl = dataManager.load(UserSogl.class).id(userSoglasovantId).one();
        Soglasovanie sogl = dataManager.load(Soglasovanie.class).id(soglId).one();
        userSogl.setComment(comment);
        String history = sogl.getHistory();
        history += "Комментарий внесен " + userSogl.getComment() + "пользователем" + userSogl.getUser().getLastName();
        sogl.setHistory(history);
        //здесь надо пересчитать
        dataManager.save(userSogl);
        dataManager.save(sogl);
        return userSogl;
    }

    //перед выходом из согласования пересчитывать решения согласовантов
    @PostMapping("/setStatus")
    public Soglasovanie setStatus(@RequestParam UUID soglId) {
        Soglasovanie s = dataManager.load(Soglasovanie.class).id(soglId).one();
        List<UserSogl> userSoglList = dataManager.load(UserSogl.class).query("select e from UserSogl e where" +
                        " e.soglasovanie =:sogl").
                parameter("sogl", s).list();

        Long count = userSoglList.stream().filter(r -> r.getStatus().equals("Согласован") && !r.getStatus().equals("На рассмотрении"))
                .count();
        if (count == userSoglList.stream().count()) {
            s.getDocVersion().getDocument().setStatus("Согласовано");
            dataManager.save(s.getDocVersion().getDocument());
        } else {
            s.getDocVersion().getDocument().setStatus("Отклонено");
            dataManager.save(s.getDocVersion().getDocument());
        }

        return s;
    }

    //отфильтровать пользователей согласовантов по вошедшему пользователю
    @GetMapping("/getSogl")
    public List<Soglasovanie> getSogl() {

        List<UserSogl> userSoglList = dataManager.load(UserSogl.class).query("select e from UserSogl e where" +
                        " e.user =: user and " +
                        "e.status = 'На рассмотрении'").
                parameter("user", ((User) currentAuthentication.getUser())).list();

        return userSoglList.stream().map(e -> e.getSoglasovanie()).toList();
    }

    //прикрепить документ к согласованию, точнее его последнюю версию
    @PostMapping("/attachLastDoc")
    public DocumentVersion attachLastDoc(@RequestBody DocumentInput input) {
        Soglasovanie soglasovanie1 = dataManager.load(Soglasovanie.class).id(input.getSoglasovanieId()).one();
        DocumentVersion last = dataManager.load(DocumentVersion.class).query("select e from DocumentVersion e where e.document =:docId order by e.createDate")
                .parameter("docId", dataManager.load(Document.class).id(input.getDocumentId()))
                .list().get(0);
        soglasovanie1.setDocVersion(last);
        dataManager.save(soglasovanie1);
        return last;
    }

    @PostMapping("/uploadExcel")
    public List<User> upload(@RequestPart("file") MultipartFile file) throws IOException {

        byte[] fileContent = file.getBytes();
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileContent));


        List<DocumentVersion> documentVersions = dataManager.load(DocumentVersion.class).all().list();
        for (DocumentVersion p : documentVersions) {
            dataManager.remove(p);
        }
        List<Document> document = dataManager.load(Document.class).all().list();
        for (Document p : document) {
            dataManager.remove(p);
        }
        List<DocRole> docRoles = dataManager.load(DocRole.class).all().list();
        for (DocRole p : docRoles) {
            p.getRedaktori().clear();
            dataManager.remove(p);
        }
        List<User> users = dataManager.load(User.class).query("select e from User e where e.username <> 'admin'").list();
        for (User user : users) {
            user.getDocRoles().clear();
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
                    .query("select e from Department e where e.num =:num").parameter("num", department).one();
            user.setDepartment(dep);

            Position pos = dataManager.load(Position.class)
                    .query("select e from Position_ e where e.shortName =:pos").parameter("pos", position).one();
            user.setPosition(pos);
            user.setPersNumberRuk(null);

            dataManager.save(user);
        }


        workbook.close();
        return dataManager.load(User.class).all().list();
    }


   public String getCellString(Cell cell) {
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








