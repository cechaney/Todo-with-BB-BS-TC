package com.thd.bbf;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;


public class TodosServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = Logger.getLogger(TodosServlet.class.getName());
       
    public TodosServlet() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String json  = null;

		LOGGER.log(Level.INFO,"GET request");
		LOGGER.log(Level.INFO,request.getRequestURI());
		
		LinkedHashMap<UUID, Todo> todos = getSessionTodos(request);
		
		if(todos != null){
			
			ObjectMapper mapper = new ObjectMapper();
			
			String rawId = request.getPathInfo().substring(1);
			
			if(rawId != null  & rawId.length() != 0){
				
				UUID id = UUID.fromString(rawId);
				
				if(id != null){
					Todo todo = todos.get(id);
					
					if(todo != null){
						json = mapper.writeValueAsString(todo);
					}

				}				
				
			} else{
			
				json = mapper.writeValueAsString(todos.values().toArray());
			}
			
			response.getWriter().write(json);
			
			LOGGER.log(Level.INFO, json.toString());
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOGGER.log(Level.INFO,"POST request");
		LOGGER.log(Level.INFO,request.getRequestURI());
		
        BufferedReader reader = request.getReader();
        
        StringBuilder sb = new StringBuilder();
        
        String line = null;
        
        while ((line = reader.readLine()) != null) {
        	sb.append(line);
        }
        
        LOGGER.log(Level.INFO, sb.toString());
        
        ObjectMapper om = new ObjectMapper();
        
        Todo todo = om.readValue(sb.toString(), Todo.class);
        
        todo.setId(UUID.randomUUID());
        
		LinkedHashMap<UUID, Todo> todos = getSessionTodos(request);
        
        if(todos == null){
        	todos = createSessionTodos(request);
        }
        
    	todos.put(todo.getId(), todo);
    	
    	response.getWriter().write(om.writeValueAsString(todo));

	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOGGER.log(Level.INFO,"PUT request");
		LOGGER.log(Level.INFO,request.getRequestURI());
		
        BufferedReader reader = request.getReader();
        
        StringBuilder sb = new StringBuilder();
        
        String line = null;
        
        while ((line = reader.readLine()) != null) {
        	sb.append(line);
        }

        LOGGER.log(Level.INFO, sb.toString());
        
        ObjectMapper om = new ObjectMapper();
        
        Todo dirtyTodo = om.readValue(sb.toString(), Todo.class);
        
		if(dirtyTodo != null && dirtyTodo.getId() != null){
			
			LinkedHashMap<UUID, Todo> todos = getSessionTodos(request);
			
			if(todos != null){
				todos.put(dirtyTodo.getId(), dirtyTodo);
			}
		}        
    	

	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOGGER.log(Level.INFO,"DELETE request");
		LOGGER.log(Level.INFO,request.getRequestURI());
		
		String rawId = request.getPathInfo().substring(1);
		
		UUID id = UUID.fromString(rawId);
		
		if(id != null){
			
			LinkedHashMap<UUID, Todo> todos = getSessionTodos(request);
			
			if(todos != null){
				todos.remove(id);
			}
		}

	}
	
	private LinkedHashMap<UUID, Todo> createSessionTodos(HttpServletRequest request){
		
		LinkedHashMap<UUID, Todo> todos = (LinkedHashMap<UUID, Todo>)request.getSession().getAttribute("todos");
		
		if(todos == null){
			todos = new LinkedHashMap<UUID, Todo>();
        	request.getSession().setAttribute("todos", todos);
		}
		
		return todos;
		
	}
	
	private LinkedHashMap<UUID, Todo> getSessionTodos(HttpServletRequest request){

		LinkedHashMap<UUID, Todo> todos = (LinkedHashMap<UUID, Todo>)request.getSession().getAttribute("todos");
		
		return todos;
	}

}
