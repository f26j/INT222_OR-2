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

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public Board getBoardById(@PathVariable String id) {
        return boardRepository.findById(id).orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
    }

    @Transactional
    public Board addBoard(@RequestBody Board board) {
        if (board.getId().equals(board.getId())) {
            throw new ApiBadRequestException("Board id '" + board.getId() + "' already exists");
        }

        if (board.getName() == null || board.getName().isEmpty() || board.getName().length() > 120) {
            throw new ApiBadRequestException("Board name cannot be empty or overlength");
        }

        return boardRepository.saveAndFlush(board);
    }

    @Transactional
    public Board editBoard(@PathVariable String id, @RequestBody Board board) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        if (boardRepository.existsById(id)) {
            board.setId(id);
            return boardRepository.saveAndFlush(board);
        }

        return null;
    }

    @Transactional
    public void deleteBoard(@PathVariable String id) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
        }
    }
}
