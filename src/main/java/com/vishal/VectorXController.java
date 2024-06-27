package com.vishal;
import com.vishal.myscale.MyScaleDataQuery;
import com.vishal.myscale.Recipe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Log
@RestController
@RequestMapping("/myscale")
public class VectorXController {

    private MyScaleDataQuery queryFinder = new MyScaleDataQuery();
    @Operation(summary = "Find recipes based on query",
            description = "Search for recipes based on a query string. This uses semantic search on embeddings " )
    @ApiResponse(responseCode = "200", description = "List of recipes found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))

    @ApiResponse(responseCode = "400", description = "Invalid query format")
    @PostMapping("/findRecipe")
    public ResponseEntity<List> findRecipe( @Schema(example = "give me paneer recipe") @RequestBody String query) {
        return new ResponseEntity<>(queryFinder.queryResult(query), HttpStatus.CREATED);
    }
    @Operation(summary = "Geta data for recipe based on name",
            description = "Search for recipes based on recipe name. "
                    )
    @ApiResponse(responseCode = "200", description = "List of recipes found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))

    @ApiResponse(responseCode = "400", description = "Invalid query format")
    @PostMapping("/findDataForRecipe")
    public ResponseEntity<List> findDataForRecipe( @Schema(example = "Noodles Masala Mixture") @RequestBody String query) {
        query =  query.replaceAll("^\"|\"$", "");
        return new ResponseEntity<>(queryFinder.queryData(query), HttpStatus.CREATED);
    }
    @PostMapping("/addRecipe")
    public ResponseEntity<Long> addRecipe(  @RequestBody Recipe recipe) {

        return new ResponseEntity<>(queryFinder.addRecipe(recipe), HttpStatus.CREATED);
    }
}
