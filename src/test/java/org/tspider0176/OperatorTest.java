package org.tspider0176;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OperatorTest {
    final String url = "jdbc:postgresql://localhost:5432/test";

    final Date DATE1 = Date.from(LocalDateTime.of(1995, 4, 15, 0, 0, 0).toInstant(ZoneOffset.UTC));
    final Date DATE2 = Date.from(LocalDateTime.of(2000, 10, 3, 0, 0, 0).toInstant(ZoneOffset.UTC));
    final Date DATE3 = Date.from(LocalDateTime.of(2005, 6, 4, 0, 0, 0).toInstant(ZoneOffset.UTC));
    final Date DATE4 = Date.from(LocalDateTime.of(2010, 5, 29, 0, 0, 0).toInstant(ZoneOffset.UTC));

    final Date FROM = Date.from(LocalDateTime.of(2000, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC));
    final Date TO = Date.from(LocalDateTime.of(2010, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC));

    /*
        $ psql
        > create database test;
        > \c test
        > CREATE TABLE student (
            id     SERIAL primary key NOT NULL,
            name   varchar(254) NOT NULL
          );

        > CREATE TABLE music (
            id      SERIAL NOT NULL,
            title   varchar(254) NOT NULL,
            release date NOT NULL
          );
     */

    @Before
    public void initialize() {
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");

        // テーブル初期化
        db.dropTable(Student.class);
        db.dropTable(Music.class);

        // 初期データinsert
        // Student
        db.insert(new Student(1, "Tom"));
        db.insert(new Student(2, "Jack"));
        db.insert(new Student(4, "Mike"));
        db.insert(new Student(3, "John"));

        // Music
        db.insert(new Music(1, "Oldstyle", DATE1));
        db.insert(new Music(3, "Rawstyle", DATE3));
        db.insert(new Music(2, "Jumpstyle", DATE2));
        db.insert(new Music(4, "Hardstyle", DATE4));
    }

    @Test
    public void testOrderByForNumber() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // ASC
        List<Student> resultASC = db.from(student)
                .orderBy(student.id)
                .select();

        assertThat(resultASC, is(Arrays.asList(
                        new Student(1, "Tom"),
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );

        // DESC
        List<Student> resultDESC = db.from(student)
                .orderByDesc(student.id)
                .select();

        assertThat(resultDESC, is(Arrays.asList(
                        new Student(4, "Mike"),
                        new Student(3, "John"),
                        new Student(2, "Jack"),
                        new Student(1, "Tom")))
        );
    }

    @Test
    public void testOrderByForDate() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Music music = new Music();

        // ASC
        List<Music> resultASC = db.from(music)
                .orderBy(music.release)
                .select();

        assertThat(resultASC.size(), is(4));
        assertThat(resultASC, is(Arrays.asList(
                        new Music(1, "Oldstyle", DATE1),
                        new Music(2, "Jumpstyle", DATE2),
                        new Music(3, "Rawstyle", DATE3),
                        new Music(4, "Hardstyle", DATE4)))
        );

        // DESC
        List<Music> resultDESC = db.from(music)
                .orderByDesc(music.release)
                .select();

        assertThat(resultDESC.size(), is(4));
        assertThat(resultDESC, is(Arrays.asList(
                        new Music(4, "Hardstyle", DATE4),
                        new Music(3, "Rawstyle", DATE3),
                        new Music(2, "Jumpstyle", DATE2),
                        new Music(1, "Oldstyle", DATE1)))
        );
    }

    @Test
    public void testOrderByForString() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // ASC
        List<Student> resultASC = db.from(student)
                .orderBy(student.name)
                .select();

        assertThat(resultASC.size(), is(4));
        assertThat(resultASC, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike"),
                        new Student(1, "Tom")))
        );

        // DESC
        List<Student> resultDESC = db.from(student)
                .orderByDesc(student.name)
                .select();

        assertThat(resultDESC.size(), is(4));
        assertThat(resultDESC, is(Arrays.asList(
                        new Student(1, "Tom"),
                        new Student(4, "Mike"),
                        new Student(3, "John"),
                        new Student(2, "Jack")))
        );
    }

    @Test
    public void testLimit() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        List<Student> result = db.from(student)
                .limit(2)
                .select();

        assertThat(result.size(), is(2));
        assertThat(result, is(Arrays.asList(
                        new Student(1, "Tom"),
                        new Student(2, "Jack")))
        );
    }

    @Test
    public void testOffset() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        List<Student> result = db.from(student)
                .offset(1)
                .limit(2)
                .orderBy(student.id)
                .select();

        assertThat(result.size(), is(2));
        assertThat(result, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John")))
        );
    }

    @Test
    public void testIsNull(){
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // IsNull用にカラムinsert
        db.insert(new Student(5, null));

        // IsNull
        List<Student> resultIsNull = db.from(student)
                // WHERE id
                .where(student.name)
                .isNull()
                .select();

        assertThat(resultIsNull.size(), is(1));
        assertThat(resultIsNull, is(Collections.singletonList(new Student(5, null))));

        // IsNotNull
        List<Student> resultIsNotNull = db.from(student)
                // WHERE id
                .where(student.name)
                .isNotNull()
                .orderBy(student.id)
                .select();

        assertThat(resultIsNotNull.size(), is(4));
        assertThat(resultIsNotNull, is(Arrays.asList(
                        new Student(1, "Tom"),
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );
    }

    @Test
    public void testInsertNull() throws Exception{
        try {
            // db接続
            com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");

            // IsNull用にカラムinsert
            db.insert(new Student(null, null));
        }
        // 例外
        catch (com.iciql.IciqlException e){
            com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
            db.dropTable(Student.class);
        }
    }

    @Test
    public void testIn(){
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // IN
        List<Student> resultIn = db.from(student)
                // WHERE id
                .where(student.id)
                // IN (2, 3, 4)
                .oneOf(2, 3, 4)
                // ORDER BY id
                .orderBy(student.id)
                .select();

        assertThat(resultIn.size(), is(3));
        assertThat(resultIn, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );

        // NOT IN
        List<Student> resultNotIn = db.from(student)
                // WHERE id
                .where(student.id)
                // IN (2, 3, 4)
                .noneOf(2, 3, 4)
                .select();

        assertThat(resultNotIn.size(), is(1));
        assertThat(resultNotIn, is(Collections.singletonList(new Student(1, "Tom"))));
    }

    @Test
    public void testSelectLikeWithEscape(){
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // Like構文を使ったSelect
        List<Student> result = db.from(student)
                .where(student.name)
                .like("J%")
                .select();

        assertThat(result.size(), is(2));
        assertThat(result, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John")))
        );
    }

    @Test
    public void testSelectLikeWithMetaEscape(){
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // Like構文を使ったSelect エスケープ指定
        // 検出用にレコードを新たにinsert
        db.insert(new Student(5, "100%grape"));

        List<Student> result = db.from(student)
                .where(student.name)
                .like("%\\%%")
                .select();

        assertThat(result.size(), is(1));
        assertThat(result, is(Collections.singletonList(new Student(5, "100%grape"))));
    }

    @Test
    public void testBetweenByDSLForNumber() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // DSLを用いたbetween
        List<Student> result = db.from(student)
                // WHERE id
                .where(student.id)
                // BETWEEN 2 AND 4
                .between(2)
                .and(4)
                // ORDER BY id
                .orderBy(student.id)
                .select();

        assertThat(result.size(), is(3));
        assertThat(result, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );
    }

    @Test
    public void testBetweenByDSLForDate() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Music music = new Music();

        List<Music> result = db.from(music)
                // WHERE release
                .where(music.release)
                // BETWEEN from AND to
                .between(FROM)
                .and(TO)
                // ORDER BY id
                .orderBy(music.id)
                .select();

        assertThat(result.size(), is(2));
        assertThat(result, is(Arrays.asList(
                        new Music(2, "Jumpstyle", DATE2),
                        new Music(3, "Rawstyle", DATE3)))
        );
    }

    @Test
    public void testBetweenByOperatorForNumber() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        // Operatorを用いたbetween
        List<Student> result = db.from(student)
                // WHERE id
                .where(student.id)
                // 1 < id AND id <= 4
                // atLeast -> 指定した数字以上
                // exceeds -> 指定した数字を超える
                .exceeds(1)
                .and(student.id)
                // atMost -> 指定した数字以下
                // lessThan -> 指定した数字未満
                .atMost(4)
                .orderBy(student.id)
                .select();

        assertThat(result.size(), is(3));
        assertThat(result, is(Arrays.asList(
                        new Student(2, "Jack"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );
    }

    @Test
    public void testBetweenByOperatorForDate() {
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Music music = new Music();

        // Operatorを用いたbetween
        List<Music> result = db.from(music)
                // WHERE id
                .where(music.release)
                // from < id AND id <= to
                // atLeast -> 指定した数字以上
                // exceeds -> 指定した数字を超える
                .atLeast(FROM)
                .and(music.release)
                // atMost -> 指定した数字以下
                // lessThan -> 指定した数字未満
                .atMost(TO)
                .orderBy(music.id)
                .select();

        assertThat(result.size(), is(2));
        assertThat(result, is(Arrays.asList(
                        new Music(2, "Jumpstyle", DATE2),
                        new Music(3, "Rawstyle", DATE3)))
        );
    }

    @Test
    public void testIsNot(){
        // db接続
        com.iciql.Db db = com.iciql.Db.open(url, "vagrant", "vagrant");
        Student student = new Student();

        List<Student> result = db.from(student)
                .where(student.id)
                .isNot(2)
                .select();

        assertThat(result.size(), is(3));
        assertThat(result, is(Arrays.asList(
                        new Student(1, "Tom"),
                        new Student(3, "John"),
                        new Student(4, "Mike")))
        );
    }
}
