package com.mapago.model.type;

import com.mapago.model.Audit;
import com.mapago.model.category.Category;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Type extends Audit {
    private Integer typeId;
    private String typeNm;
    private Integer categoryId;
    private Integer dispOrd;
    private String typeForm;
    private String typeFormNm;
    private String typeOrdMethod;
    private String typeOrdMethodNm;
    private Category category;
    private List<TypeDetail> typeDetailList;
}