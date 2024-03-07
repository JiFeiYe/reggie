package com.tu.dto;

import com.tu.entity.Dish;
import com.tu.entity.DishFlavor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JiFeiYe
 * @since 2024/3/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DishDto extends Dish {

    List<DishFlavor> flavors = new ArrayList<>();

    String categoryName;
}
