//package sit.int221.BEOR2.db1.controllers;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import sit.int221.BEOR2.BeOr2CrossOrigin;
//import sit.int221.BEOR2.db1.dtos.BoardDto;
//import sit.int221.BEOR2.db1.entities.Board;
//import sit.int221.BEOR2.db1.services.BoardService;
//import org.springframework.validation.annotation.Validated;
//
//import java.util.List;
//import java.util.Map;
//
//@Validated
//@RestController
//@RequestMapping("/v3/boards")
//@BeOr2CrossOrigin
//public class BoardController {
//    @Autowired
//    private BoardService boardService;
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    public List<BoardDto> getBoards() {
//        return boardService.getBoards();
//    }
//
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public BoardDto getBoardById(@PathVariable String id) {
//        System.out.println("board Controller");
//        System.out.println(id);
//        return modelMapper.map(boardService.getBoardById(id), BoardDto.class);
//    }
//
//    @PostMapping("")
//    @ResponseStatus(HttpStatus.CREATED)
//    public BoardDto addBoard(@RequestBody Board board) {
//        return modelMapper.map(boardService.addBoard(board), BoardDto.class);
//    }
//
//    @PutMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public BoardDto editBoard(@PathVariable String id, @RequestBody(required = false) Board board) {
//        return modelMapper.map(boardService.editBoard(id, board), BoardDto.class);
//    }
//
//    @PatchMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public BoardDto editBoardVisibility(@PathVariable String id, @RequestBody(required = false) Map<String, String> requestBody) {
//        if (requestBody != null) {
//            String visibility = requestBody.get("visibility");
//            return boardService.editBoardVisibility(id, visibility);
//        } else {
//            return boardService.editBoardVisibility(id, null);
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteBoard(@PathVariable String id) {
//        boardService.deleteBoard(id);
//    }
//}

package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.services.BoardService;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/v3/boards")
@BeOr2CrossOrigin
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardDto> getBoards() {
        return boardService.getBoards();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto getBoardById(@PathVariable String id) {
        return boardService.getBoardById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDto addBoard(@RequestBody Board board) {
        return boardService.addBoard(board);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto editBoard(@PathVariable String id, @RequestBody(required = false) Board board) {
        return boardService.editBoard(id, board);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto editBoardVisibility(@PathVariable String id, @RequestBody(required = false) Map<String, String> requestBody) {
        String visibility = (requestBody != null) ? requestBody.get("visibility") : null;
        return boardService.editBoardVisibility(id, visibility);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
    }
}
