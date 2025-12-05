package com.notesapi.service;

import com.notesapi.dto.NoteRequest;
import com.notesapi.dto.NoteResponse;
import com.notesapi.model.Note;
import com.notesapi.model.User;
import com.notesapi.repository.NoteRepository;
import com.notesapi.security.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

//    get current user logged data
    private User getCurrentUserData(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return customUserDetailService.loadUserEntityByEmail(userEmail);
    }

//    convert note entity into NoteResponse DTO
private NoteResponse convertToResponse(Note note) {
    NoteResponse response = new NoteResponse();
    response.setId(note.getId());
    response.setTitle(note.getTitle());
    response.setContent(note.getContent());
    response.setCreatedAt(note.getCreatedAt());
    response.setUpdatedAt(note.getUpdatedAt());

    System.out.println("Converting to response - Content: " + note.getContent());
    System.out.println("Response object: " + response);

    return response;
}

//    Create note
public NoteResponse createNote(NoteRequest request) {
    User user = getCurrentUserData();

    // DEBUG - Check what we received
    System.out.println("=== DEBUG CREATE NOTE ===");
    System.out.println("Request Title: " + request.getTitle());
    System.out.println("Request Content: " + request.getContent());
    System.out.println("Request object: " + request);

    Note note = new Note();
    note.setTitle(request.getTitle());
    note.setContent(request.getContent());
    note.setUser(user);

    System.out.println("Note before save - Title: " + note.getTitle());
    System.out.println("Note before save - Content: " + note.getContent());

    Note savedNote = noteRepository.save(note);

    System.out.println("Note after save - ID: " + savedNote.getId());
    System.out.println("Note after save - Title: " + savedNote.getTitle());
    System.out.println("Note after save - Content: " + savedNote.getContent());
    System.out.println("=== END DEBUG ===");

    return convertToResponse(savedNote);
}

//    get all notes current user
    public List<NoteResponse> getAllNotes(){
        User currentUserData = getCurrentUserData();
        List<Note> noteByUserId = noteRepository.findByUserId(currentUserData.getId());

        return noteByUserId.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }

//    get notes by single user id
    public NoteResponse getNotesById(Long userId){
        User currentUserData = getCurrentUserData();

        Note noteByUserId = noteRepository.findById(userId).orElseThrow(()-> new RuntimeException("Note not found"));

//        check if note belongs to current user
        if(!noteByUserId.getUser().getId().equals(currentUserData.getId())){
            throw new RuntimeException("You dont have permission to access this note");
        }

        return convertToResponse(noteByUserId);
    }


//    Update note

    public NoteResponse updateNote(Long userId, NoteRequest noteRequest){
        User currentUserData = getCurrentUserData();

        Note noteByUserId = noteRepository.findById(userId).orElseThrow(() -> new RuntimeException("Note not found"));

//        check if note belongs to current user
        if(!noteByUserId.getUser().getId().equals(currentUserData.getId())){
            throw new RuntimeException("You dont have permission to access this note");
        }

        noteByUserId.setTitle(noteRequest.getTitle());
        noteByUserId.setContent(noteRequest.getContent());

        Note updatedNote = noteRepository.save(noteByUserId);
        return convertToResponse(updatedNote);

    }


//    delete note
    public void deleteNote(Long userId){
        User currentUserData = getCurrentUserData();

        Note noteByUserId = noteRepository.findById(userId).orElseThrow(() -> new RuntimeException("Note not found"));

//        check if note belongs to current user
        if(!noteByUserId.getUser().getId().equals(currentUserData.getId())){
            throw new RuntimeException("You don't have permission to delete this note");

        }

        noteRepository.delete(noteByUserId);

    }

}
