package com.HotelManagement.HotelManagement.controller;


import com.HotelManagement.HotelManagement.dto.Response;
import com.HotelManagement.HotelManagement.service.interfacE.IBookingService;
import com.HotelManagement.HotelManagement.service.interfacE.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IBookingService iBookingService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addNewRoom(
            @RequestParam(value = "photo", required = false)MultipartFile photo,
            @RequestParam(value = "roomType", required = false)String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false)String roomDescription
            ){

        if(photo==null|| photo.isEmpty()|| roomType==null||roomType.isBlank()||roomPrice==null||roomType.isBlank()){
            Response response =new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide the values for all fields(photo,roomType,roomPrice,Room description");
        }
        Response response = roomService.addNewRoom(photo, roomType, roomPrice, roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllUsers(){
        Response response = roomService.getAllRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();

    }

    @GetMapping("/room-by-id/{roomId}")
    public ResponseEntity<Response> getRoomById(@PathVariable Long roomId){
        Response response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-available-rooms")
    public ResponseEntity<Response> getAvailableRooms(){
        Response response = roomService.getAllAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available-rooms-by-date-and-type")
    public ResponseEntity<Response> getAvailableRoomsByDateAndType(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkingDate,
            @RequestParam( required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkoutDate,
            @RequestParam(required = false) String roomType
    ){

        if(checkingDate==null|| roomType==null||roomType.isBlank()||checkoutDate==null){
            Response response =new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide the values for all fields(checkinDate,roomType,checkoutDate");
        }
        Response response = roomService.getAvailableRoomsByDateAndType(checkingDate, checkoutDate, roomType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(@PathVariable Long id,
                                               @RequestParam(value = "photo", required = false)MultipartFile photo,
                                               @RequestParam(value = "roomType", required = false)String roomType,
                                               @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
                                               @RequestParam(value = "roomDescription", required = false)String roomDescription

    ){
        Response response = roomService.updateRoom(id,roomDescription,roomType,roomPrice,photo);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable Long roomId){
        Response response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }




}
