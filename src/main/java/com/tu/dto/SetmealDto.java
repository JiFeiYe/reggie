package com.tu.dto;

import com.tu.entity.Setmeal;
import com.tu.entity.SetmealDish;
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
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes = new ArrayList<>();

    private String categoryName;
}
