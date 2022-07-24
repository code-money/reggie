package cn.zyz.dto;

import cn.zyz.entity.Setmeal;
import cn.zyz.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
