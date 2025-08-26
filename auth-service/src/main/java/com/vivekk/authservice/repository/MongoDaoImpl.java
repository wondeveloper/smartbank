package com.vivekk.authservice.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.*;
import com.vivekk.authservice.entity.Products;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Data
@AllArgsConstructor
public class MongoDaoImpl {

    private MongoTemplate mongoTemplate;

    public void bulkUpdate(List<String> ids, String newStatus) {
        List<UpdateOneModel<Document>> updates = ids.stream()
                .map(id -> new UpdateOneModel<Document>(
                        Filters.eq("_id", new ObjectId(id)),
                        Updates.set("status", newStatus)
                ))
                .toList();


        BulkWriteResult result = mongoTemplate
                .getCollection("products")
                .bulkWrite(updates, new BulkWriteOptions().ordered(false));

        System.out.println("Modified count: " + result.getModifiedCount());

//        List<WriteModel<Document>> writes = List.of(new UpdateOneModel<>(Filters.eq("_id", 1),Updates.set("status", "true")),
//                new UpdateManyModel<>(Filters.eq("_id", 1),Updates.set("status", "true")));
//
//        BulkWriteResult result1 = mongoTemplate.getCollection("products")
//                .bulkWrite(writes, new BulkWriteOptions().ordered(false));
    }

    public void bulkUpsert(List<Products> products){

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        for (Products product : products){
            Document document = new Document();
            mongoTemplate.getConverter().write(product, document);

            Update update = new Update();
            document.forEach(update::set);

            bulkOperations.add(new UpdateOneModel<>(Filters.eq("_id", product.getId()),update.getUpdateObject(),new UpdateOptions().upsert(true)));
        }

        mongoTemplate.getCollection("products").bulkWrite(bulkOperations, new BulkWriteOptions().ordered(false));
    }

}
