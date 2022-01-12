package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodeService {

    private final CodeRepository codeRepository;

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");

    @Autowired
    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Code findCodeById(Long id) {
        return codeRepository.findCodeById(id);
    }

    public Code findCodeByUuid(String uuid){
        return codeRepository.findCodeByUuid(uuid);
    };


    public Code save(Code toSave) {
        toSave.setDate(LocalDateTime.now().format(format));
        return codeRepository.save(toSave);
    }

    public List<Code> findAll() {
        return codeRepository.findAll();
    }

    public List<Code> findAllByTimeRestrictedAndViewsRestricted() {
        return codeRepository.findAllByTimeRestrictedAndViewsRestricted(false, false);
    }

    public boolean check(Code code) {
        boolean ok = true;
        if (code.isViewsRestricted()) {

            if (code.getViews()<1) {
                ok = false;
            }
            code.increaseView();
            codeRepository.save(code);
        }
        if (code.isTimeRestricted()) {

            int secondsBetween = (int) Duration.between(LocalDateTime.now(), LocalDateTime.parse(code.getDate(), format).plusSeconds(code.getTime())).toSeconds();

            if (secondsBetween<=1) {
                ok = false;
            }
        }
        if (!ok) {
            codeRepository.delete(code);
        }
        return ok;
    }

}
