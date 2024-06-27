package com.vishal.myscale;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Schema (example = "Paneer do pyaza")
    String receipeName;
    @Schema (example = "lunch")
    String category;
    @Schema (example = "30 mins")
    String totalTime;
    @Schema (example = "fry the paneer and add the gravy, add some cashews and raisin to make it bit sweet")
    String method;
    @Schema (example = "paneer, milk  , cashews")
    String ingridents;
}
