package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@Controller
public class CodeSharingPlatform {

    public static void main(String[] args) {
        SpringApplication.run(CodeSharingPlatform.class, args);
    }

    @Autowired
    CodeService codeService;

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");



    @RequestMapping(value = "/code/{uuid}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public String code(@PathVariable String uuid) {

        Optional<Code> codeOpt = Optional.ofNullable(codeService.findCodeByUuid(uuid));
        if (codeOpt.isPresent()) {
            if (codeService.check(codeService.findCodeByUuid(uuid))) {
                Code foundCode = codeService.findCodeByUuid(uuid);
                String timeRestriction = "";
                String viewsRestriction = "";
                if (foundCode.isTimeRestricted()) {
                    int secondsBetween = (int) Duration.between(LocalDateTime.now(), LocalDateTime.parse(foundCode.getDate(), format).plusSeconds(foundCode.getTime())).toSeconds();
                    foundCode.setTime(secondsBetween <=0 ? 0 : secondsBetween);
                    timeRestriction = "<span id=\"time_restriction\">" + foundCode.getTime() +  "</span>";
                }
                if (foundCode.isViewsRestricted()) {
                    viewsRestriction = "<span id=\"views_restriction\">" + foundCode.getViews() +  "</span>";
                }

                return "<html>\n" + "<head><title>Code</title><link rel=\"stylesheet\"\n" +
                        "       target=\"_blank\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                        "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                        "<script>hljs.initHighlightingOnLoad();</script></head>\n" +
                        "<body>\n" + "<pre id=\"code_snippet\">\n<code>\n" + foundCode.getCode() + "</code>\n</pre>\n" +
                        "<span id=\"load_date\">" + foundCode.getDate() + "</span>" +
                        timeRestriction + viewsRestriction +
                        "</body>\n" + "</html>";
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/api/code/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public Code apiCode(@PathVariable String uuid) {

        Optional<Code> codeOpt = Optional.ofNullable(codeService.findCodeByUuid(uuid));
        if (codeOpt.isPresent()) {
            System.out.println("DEBUG" + codeService.findCodeByUuid(uuid).getDate());
            if (codeService.check(codeService.findCodeByUuid(uuid))) {
                Code foundCode = codeService.findCodeByUuid(uuid);
                int secondsBetween = (int) Duration.between(LocalDateTime.now(), LocalDateTime.parse(foundCode.getDate(), format).plusSeconds(foundCode.getTime())).toSeconds();
                foundCode.setTime(secondsBetween <=0 ? 0 : secondsBetween);
                return foundCode;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }



    @RequestMapping(value = "/api/code/latest", method = RequestMethod.GET)
    @ResponseBody
    public List<Code> apiCodes() {
        return codeService.findAllByTimeRestrictedAndViewsRestricted().stream()
                .sorted(Comparator.comparing(Code::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/code/latest")
    public String pageWithCodes(ModelMap model) {
        List<Code> latest10 = codeService.findAllByTimeRestrictedAndViewsRestricted().stream()
                .sorted(Comparator.comparing(Code::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("codeList", latest10);
        return "page";
    }



    @RequestMapping(value = "/api/code/new", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> newCode(@RequestBody Map<String, String> newCode){
        Code addedCode = codeService.save(new Code(newCode.get("code"), Integer.parseInt(newCode.get("time")), Integer.parseInt(newCode.get("views"))));
        return Map.of("id", addedCode.getUuid());
    }

    @GetMapping(value = "/code/new", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String codeNew() {
        return "<html>\n" + "<header><title>Create</title></header>\n" +
                "<body>\n" + "<textarea id=\"code_snippet\"> Text </textarea>" +
                "<input id=\"time_restriction\" type=\"text\"/>"+
                "<input id=\"views_restriction\" type=\"text\"/>" +
                "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>" +
                "</body>\n" + "</html>" + "<script> function send() {\n" +
                "    let object = {\n" +
                "        \"code\": document.getElementById(\"code_snippet\").value\n" +
                "        \"time\": document.getElementById(\"time_restriction\").value\n" +
                "        \"views\": document.getElementById(\"views_restriction\").value\n" +
                "    };\n" +
                "    \n" +
                "    let json = JSON.stringify(object);\n" +
                "    \n" +
                "    let xhr = new XMLHttpRequest();\n" +
                "    xhr.open(\"POST\", '/api/code/new', false)\n" +
                "    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');\n" +
                "    xhr.send(json);\n" +
                "    \n" +
                "    if (xhr.status == 200) {\n" +
                "      alert(\"Success!\");\n" +
                "    }\n" +
                "}</script>";
    }

}
