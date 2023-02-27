package academy.prog;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
public class UrlController {
    private final UrlService urlService;
    private final UrlRepository urlRepository;

    public UrlController(UrlService urlService,
                         UrlRepository urlRepository) {
        this.urlService = urlService;
        this.urlRepository = urlRepository;
    }
    @ResponseBody
    @PostMapping("shorten")
    public UrlResultDTO shorten(@RequestBody UrlDTO urlDTO) {
        long id = urlService.saveUrl(urlDTO);

        var result = new UrlResultDTO();
        result.setUrl(urlDTO.getUrl());
        result.setShortUrl(Long.toString(id));

        return result;
    }
    @ResponseBody
    @GetMapping("my/{id}")
    public ResponseEntity<Void> redirect(@PathVariable("id") Long id) {
        var url = urlService.getUrl(id);

        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    @ResponseBody
    @GetMapping("stat")
    public List<UrlStatDTO> stat() {
        return urlService.getStatistics();
    }


    @PostMapping("/result")
    public String index(@RequestParam String url, Model model) {
        UrlDTO urlDTO = new UrlDTO();
        urlDTO.setUrl(url);
        long id = urlService.saveUrl(urlDTO);
        model.addAttribute("shortURL", id);
        return "index";
    }

    @ResponseBody
    @GetMapping("del/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (urlService.findById(id).isPresent()){
            urlService.deleteById(id);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("ID", " is deleted");
        return new ResponseEntity<>(httpHeaders,HttpStatus.OK);
    }
}
