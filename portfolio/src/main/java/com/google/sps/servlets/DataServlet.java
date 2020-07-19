// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

    /**
     * Retrieves comments from database and returns them in JSON format sorted 
     * in descending order by time when they were added to database.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery queryResults = datastore.prepare(query);
        List<String> comments = new ArrayList<>();
        for (Entity entity : queryResults.asIterable()) {
            String comment = (String) entity.getProperty("commentBody");
            comments.add(comment);
        }
        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(comments));
    }

    /** 
     * Adds comments to database and redirects to home page.
     *
     * A Comment has following attributes:
     * - body: Body of the comment
     * - timestamp: Time when the server received comment from the client
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String commentBody = request.getParameter("commentBody");
        long timestamp = System.currentTimeMillis();
        Entity commentEntity = new Entity("comment");
        commentEntity.setProperty("commentBody", commentBody);
        commentEntity.setProperty("timestamp", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
        response.sendRedirect("/index.html");
    }
}
