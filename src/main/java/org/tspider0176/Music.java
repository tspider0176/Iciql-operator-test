package org.tspider0176;


import com.iciql.Iciql;

import java.util.Date;
import java.util.Objects;

@Iciql.IQTable(name = "Music")
public class Music {
    @Iciql.IQColumn(name = "ID", nullable = false, primaryKey = true)
    public Integer id;
    @Iciql.IQColumn(name = "TITLE", nullable = false)
    public String title;
    @Iciql.IQColumn(name = "RELEASE", nullable = false)
    public Date release;

    public Music(){}

    public Music(Integer id, String title, Date release){
        this.id = id;
        this.title = title;
        this.release = release;
    }

    public boolean equals(Object target){
        if(this == target) return true;
        else if(!(target instanceof Music)){
            return false;
        }else{
            Music targetBook = (Music) target;
            return Objects.equals(this.id, targetBook.id) &&
                    Objects.equals(this.title, targetBook.title);
        }
    }

    @Override
    public String toString(){
        return title + "(" + id + ")" + "release at" + release.toString();
    }
}
