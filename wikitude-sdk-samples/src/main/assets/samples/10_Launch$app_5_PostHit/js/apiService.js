function API(token){

    this.api_point = "http://post-hit-prod.eu-west-1.elasticbeanstalk.com:8000/api/";
    this.token = token;

    this.getBoardsAroundMe = function(longitude, latitude, World){
    var boards;
        $.ajax({
        url: this.api_point+"display_board/longitude/"+longitude+"/latitude/"+latitude,
        type: 'GET',
         beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer '+token);
        },
        success: function (data) {
            if (typeof data !== "undefined"){
                data.forEach(function(board){
                    World.boards.push({
                         "id": board.id,
                         "longitude": board.longitude,
                         "latitude": board.latitude,
                         "name": board.name,
                         "altitude": board.altitude,
                         "description": board.description,
                         "nbPostHits": board.post_hit_nb,
                        "created_at": "createdate",
                        "created_by": "author",
                        "updated_at": "updatedate",
                        "updated_by": "update author",
                        "picture": "assets/default-picture.png",
                        "status": "Active",
                    });
                });
            }
            World.loadPoisFromJsonData(World.boards);
        },
        error: function (err) {
            World.loadPoisFromJsonData(World.boards);
         }
        });
    };

    this.getBoardInfos = function(id_board, World){
        $.ajax({
        url: this.api_point+"display_board/"+id_board,
        type: 'GET',
         beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer '+token);
        },
        success: function (data) {
            console.log("data");
            console.log(data);
            data.post_hits.forEach(function(d){
                d.user = d.user.pseudo;
                d.titre = d.title;
                d.likes = d.nb_likes;
                d.dislikes = d.nb_dislikes;
            });
            World.loadOnOpenBoard(data);
        },
        error: function (err) {
            console.log("err");
            console.log(err);
         }
        });
    };

    this.postPostHit = function(title, message, tags, World){
        var finalTags = [];
        tags.forEach(function(t){
            finalTags.push( "#"+t.trim());
        });
        $.ajax({
            url: this.api_point+"post_hit/",
            contentType: 'application/json',
            type: 'POST',
             beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer '+token);
            },
            data: JSON.stringify({
              "title": title,
              "message": message,
              "display_board_id": World.currentBoard.id,
              "tags": finalTags
            }),
            success: function (data) {
                World.validateSubmitAddPosthit();
            },
            error: function (err) {
                console.log("err");
                console.log(err);
                $.toast({
                     heading: "Error",
                     text: "Error while creating new PostHit. Maybe you already wrote one on this board?",
                     textAlign: 'center',
                     position: 'bottom-center',
                });
             }
        });
    };

    this.postOpinion = function(opinion_type, World){
        $.ajax({
            url: this.api_point+"like_post_hit/",
            contentType: 'application/json',
            type: 'POST',
             beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer '+token);
            },
            data: JSON.stringify({
                "post_hit_id": World.currentPostHit.id,
                "opinion_type_name": opinion_type
            }),
            success: function (data) {
                World.validatePostOpinion();
            },
            error: function (err) {
                $.toast({
                     heading: "Error",
                     text: "You cannot like or dislike a posthit you already liked or disliked.",
                     textAlign: 'center',
                     position: 'bottom-center',
                });
             }
        });
    };
}
