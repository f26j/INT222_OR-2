package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.services.BoardService;
import org.springframework.validation.annotation.Validated;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/v3/boards")
@BeOr2CrossOrigin
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardDto> getBoards(@RequestParam Long ownerOid) {
        List<Board> boards = boardService.getBoardsByOwnerOidOrPublic(ownerOid);
        return boards.stream().map(board -> new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(
                        board.getUser().getOid(),
                        board.getUser().getUsername()
                )
        )).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BoardDto> getBoardById(@PathVariable String id) {
        try {
            BoardDto boardDto = modelMapper.map(boardService.getBoardById(id), BoardDto.class);
            return ResponseEntity.ok(boardDto);
        } catch (ApiForbiddenException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDto addBoard(@Valid @RequestBody Board board) {
        return modelMapper.map(boardService.addBoard(board), BoardDto.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto editBoard(@PathVariable String id, @Valid @RequestBody Board board) {
        return modelMapper.map(boardService.editBoard(id, board), BoardDto.class);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto editBoardVisibility(@PathVariable String id, @RequestBody @Valid Map<String, String> requestBody) {
        String visibility = requestBody.get("visibility");
        return boardService.editBoardVisibility(id, visibility);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
    }

    // เพิ่ม @ExceptionHandler สำหรับ ApiForbiddenException เพื่อจัดการ 403 Forbidden
    @ExceptionHandler(ApiForbiddenException.class)
    public ResponseEntity<String> handleApiForbiddenException(ApiForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
