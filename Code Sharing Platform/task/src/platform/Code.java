package platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;




@Entity
@Table(name = "code")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    String code;
    String date;
    @JsonIgnore
    String uuid;
    @JsonIgnore
    boolean timeRestricted;
    @JsonIgnore
    boolean viewsRestricted;
    int time;
    int views;

    public Code() {
    }

    public Code(String code, int time, int views) {
        this.code = code;
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.timeRestricted = false;
        this.viewsRestricted = false;


        if (time <= 0) {
            this.time = 0;
        } else {
            this.timeRestricted = true;
            this.time = time;
        }

        if (views <= 0) {
            this.views = 0;
        } else {
            this.viewsRestricted = true;
            this.views = views;
        }

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isTimeRestricted() {
        return timeRestricted;
    }

    public boolean isViewsRestricted() {
        return viewsRestricted;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void increaseView() {
        int currentViews = this.views;
        this.views = currentViews-1;
    }
}
