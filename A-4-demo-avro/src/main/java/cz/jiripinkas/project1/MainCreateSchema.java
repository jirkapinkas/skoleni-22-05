package cz.jiripinkas.project1;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class MainCreateSchema {

    public static void main(String[] args) {
        Schema schema = SchemaBuilder.record("Movie")
                .namespace("cz.jiripinkas.project1")
                .fields()
                .requiredString("title")
                .requiredInt("year")
                .endRecord();

        System.out.println(schema);
    }

}
