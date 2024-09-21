package sit.int221.BEOR2.db1.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public List<Board> getBoardById(@PathVariable String id) {
        return Collections.singletonList(boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found")));
    }

    @Transactional
    public Board addBoard(@RequestBody Board board) {
        if (board.getUser() == null) {
            throw new ApiBadRequestException("User is required to create a board");
        }

        if (boardRepository.existsById(board.getId())) {
            throw new ApiBadRequestException("Board id '" + board.getId() + "' already exists");
        }

        String boardName = board.getName();
        if (boardName == null || boardName.trim().isEmpty() || boardName.length() > 120) {
            throw new ApiBadRequestException("Board name cannot be empty or overlength");
        }

        return boardRepository.saveAndFlush(board);
    }

    @Transactional
    public Board editBoard(@PathVariable String id, @RequestBody Board board) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        board.setId(existingBoard.getId());
        return boardRepository.saveAndFlush(existingBoard);
    }

    @Transactional
    public void deleteBoard(@PathVariable String id) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        boardRepository.deleteById(id);
    }
}
