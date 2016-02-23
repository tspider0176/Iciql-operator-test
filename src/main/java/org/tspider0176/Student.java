package org.tspider0176;

import java.util.Objects;
import com.iciql.Iciql;

@Iciql.IQTable(name="Student")
public class Student {
    @Iciql.IQColumn(name = "ID", nullable = true, primaryKey = true)
    public Integer id;
    @Iciql.IQColumn(name = "NAME", nullable = true)
    public String name;

    public Student() {}

    public Student(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public boolean equals(Object target) {
        if (this == target) return true;
        else if (!(target instanceof Student)) {
            return false;
        } else {
            Student castedTarget = (Student) target;
            return Objects.equals(this.id, castedTarget.id) &&
                    Objects.equals(this.name, castedTarget.name);
        }
    }

    public String toString(){
        return "Student(" + id + ": " + name + ")";
    }
}