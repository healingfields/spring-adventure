package ma.showmaker.coffeapp;

import org.apache.coyote.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CoffeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeAppApplication.class, args);
    }
}



@ConfigurationProperties(prefix = "greeting")
class Greeting{
    private String name;
    private String coffe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoffe() {
        return coffe;
    }

    public void setCoffe(String coffe) {
        this.coffe = coffe;
    }
}

@RestController
@RequestMapping("/greeting")
class GreetingController{
    private final Greeting greeting;

    public GreetingController(Greeting greeting){
        this.greeting = greeting;
    }

    @GetMapping
    String getGreeting(){
        return greeting.getName();
    }

    @GetMapping("/coffe")
    String getGreetingAndCoffe(){
        return greeting.getCoffe();
    }

}

@RestController
@RequestMapping("/coffes")
class CoffeApiController{

    final CoffeRepository coffeRepository;

    public CoffeApiController(CoffeRepository repository){
        this.coffeRepository = repository;
    }

    @GetMapping
    Iterable<Coffe> getCoffes(){
        return this.coffeRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Coffe> getCoffeById(@PathVariable String id){
        return this.coffeRepository.findById(id);
    }

    @PostMapping
    Coffe addCoffe(@RequestBody Coffe coffe){
        return this.coffeRepository.save(coffe);
    }

    @PutMapping("/{id}")
    ResponseEntity<Coffe> updateCoffe(@PathVariable String id, @RequestBody Coffe coffe){
        return (this.coffeRepository.existsById(id))
                ? new ResponseEntity<Coffe>(this.coffeRepository.save(coffe), HttpStatus.OK)
                : new ResponseEntity<Coffe>(this.coffeRepository.save(coffe), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    void deleteCoffe(@PathVariable String id){
        this.coffeRepository.deleteById(id);
    }

}

@Component
class DataLoader {

    final CoffeRepository coffeRepository;

    public DataLoader(CoffeRepository coffeRepository){
        this.coffeRepository = coffeRepository;
    }

    @PostConstruct
    private void LoadData(){
        this.coffeRepository.saveAll(List.of(
                new Coffe("brini"),
                new Coffe("maria"),
                new Coffe("etoile")
        ));
    }
}
interface CoffeRepository extends CrudRepository<Coffe, String>{};

@Entity
class Coffe{

    @Id
    private String id;
    private String name;

    public Coffe(){

    }

    public Coffe(String id, String name){
        this.id = id;
        this.name = name;
    }

    public Coffe(String name){
        this(UUID.randomUUID().toString(), name);
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }
}
