// implementation of AR-Experience (aka "World")
var World = {

    boards : [],
	//  user's latest known location, accessible via userLocation.latitude, userLocation.longitude, userLocation.altitude
	userLocation: null,
	// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,
	markerList: [],
	// The last selected marker
	currentMarker: null,
	currentBoard: null,
	reportedTab : [],

    apiService : null,
    bearerToken: "",
	locationUpdateCounter: 0,
	updatePlacemarkDistancesEveryXLocationUpdates: 10,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		AR.context.destroyAll();
		PoiRadar.show();
		$('#radarContainer').unbind('click');
		$("#radarContainer").click(PoiRadar.clickedRadar);
        AR.logger.info("Loading "+poiData.length+" POI");

		// empty list of visible markers
		World.markerList = [];

		// start loading marker assets
		World.markerDrawable_idle = World.markerDrawable_idle ? World.markerDrawable_idle : new AR.ImageResource("assets/board.png");
		World.markerDrawable_selected = World.markerDrawable_selected ? World.markerDrawable_selected : new AR.ImageResource("assets/board_selected.png");
		World.markerDrawable_directionIndicator = World.markerDrawable_directionIndicator ? World.markerDrawable_directionIndicator: new AR.ImageResource("assets/indi.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude)+ (Math.random() / 5 - 0.1),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude)+ (Math.random() / 5 - 0.1),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description,
				"nbPostHits": poiData[currentPlaceNr].nbPostHits,
                "picture": poiData[currentPlaceNr].picture,
                "status": poiData[currentPlaceNr].status
			};
			World.markerList.push(new Marker(singlePoi));
		}
		// updates distance information of all placemarks
		World.updateDistanceToUserValues();
/*            $.toast({
                 heading: "info",
                 text: poiData.length + "boards loaded",
                 textAlign: 'center',
                 position: 'bottom-center',
            });
*/
		// set distance slider to 100%
		$("#panel-distance-range").val(100);
		$("#panel-distance-range").slider("refresh");
	},

	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
		for (var i = 0; i < World.markerList.length; i++) {
			World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
		}
	},

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
	AR.logger.info("Location changed");

		// store user's current location in World.userLocation, so you always know where user is
		World.userLocation = {
			'latitude': lat,
			'longitude': lon,
			'altitude': alt,
			'accuracy': acc
		};
		// request data if not already present
		if (!World.initiallyLoadedData) {
			World.requestDataFromServer(lat, lon);
			World.initiallyLoadedData = true;
		} else if (World.locationUpdateCounter === 0) {
			// update placemark distance information frequently, you max also update distances only every 10m with some more effort
			World.updateDistanceToUserValues();
		}

		// helper used to update placemark information every now and then (e.g. every 10 location upadtes fired)
		World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
	},

	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {
		World.currentMarker = marker;
        AR.logger.error("Current board id: " + marker.poiData.id);

        this._post_hit_local_tab = [];
        this._current_display_board = marker.poiData;
        World.onOpenBoard(marker.poiData.id);

		if( undefined == marker.distanceToUser ) {
			marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
		}
		var distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");

		$(".poi-detail-distance").each(function(){
		    $(this).html(distanceToUserValue);
		});

		$(".ui-panel-dismiss").unbind("mousedown");

		$("#panel-test").on("panelbeforeclose", function(event, ui) {
			World.currentBoard = null;
			World.currentMarker.setDeselected(World.currentMarker);
		});
	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
	},

	// returns distance in meters of placemark with maxdistance * 1.1
	getMaxDistance: function getMaxDistanceFn() {
		World.markerList.sort(World.sortByDistanceSortingDescending);
		var maxDistanceMeters = World.markerList[0].distanceToUser;
		return maxDistanceMeters * 1.1;
	},

	// udpates values show in "range panel"
	updateRangeValues: function updateRangeValuesFn() {

		var slider_value = $("#panel-distance-range").val();
		var maxRangeMeters = Math.round(World.getMaxDistance() * (slider_value / 100));
		var maxRangeValue = (maxRangeMeters > 999) ? ((maxRangeMeters / 1000).toFixed(2) + " km") : (Math.round(maxRangeMeters) + " m");
		var placesInRange = World.getNumberOfVisiblePlacesInRange(maxRangeMeters);
		$("#panel-distance-value").html(maxRangeValue);
		$("#panel-distance-places").html((placesInRange != 1) ? (placesInRange + " Places") : (placesInRange + " Place"));
		AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);
		PoiRadar.setMaxDistance(Math.max(maxRangeMeters, 1));
	},

	// returns number of places with same or lower distance than given range
	getNumberOfVisiblePlacesInRange: function getNumberOfVisiblePlacesInRangeFn(maxRangeMeters) {

		World.markerList.sort(World.sortByDistanceSorting);
		for (var i = 0; i < World.markerList.length; i++) {
			if (World.markerList[i].distanceToUser > maxRangeMeters) {
				return i;
			}
		};
		return World.markerList.length;
	},

	handlePanelMovements: function handlePanelMovementsFn() {

		$("#panel-distance").on("panelclose", function(event, ui) {
			$("#radarContainer").addClass("radarContainer_left");
			$("#radarContainer").removeClass("radarContainer_right");
			$('#loader').show();
			PoiRadar.updatePosition();
		});

		$("#panel-distance").on("panelopen", function(event, ui) {
			$("#radarContainer").removeClass("radarContainer_left");
			$("#radarContainer").addClass("radarContainer_right");
			PoiRadar.updatePosition();
		});
	},

	// display range slider
	showRange: function showRangeFn() {
		if (World.markerList.length > 0) {

			$('#panel-distance-range').change(function() {
				World.updateRangeValues();
			});
			World.updateRangeValues();
			World.handlePanelMovements();
			$("#panel-distance").trigger("updatelayout");
			$("#panel-distance").panel("open", 1234);
		} else {
			$.toast({
				 heading: "Info",
				 text: "No location is visible yet.",
				 textAlign: 'center',
				 position: 'bottom-center',
			});
		}
	},

    printValues: function printValues(obj) {
         if (obj.length == 0){
             AR.logger.error("Empty object.");
         }
         else{
             for (var key in obj) {
                 if (typeof obj[key] === "object") {
                     AR.logger.info("Object: " + key + ": ");
                     printValues(obj[key]);
                 } else {
                     AR.logger.info("'" + key + "' = '" + obj[key] + "'");
                 }
             }
         }
     },

    generateDivForPosthit: function generateDivForPostHitFn(posthit){ // posthit.post_hit.id, posthit.post_hit.message, posthit.post_hit.reputation, posthit.post_hit.pseudo

        var newDiv =
        '<li>'
        + '<div>'
        +   '<div>'
        +       '<h1><span>'+ posthit.titre +'</span></h1>'
        +       '<p style="text-align:left">';
        posthit.tags.forEach(function(t){
            newDiv += '<span class="tag">'+ t +'</span><br/>';
        });
        // generation tags
        newDiv += '</p>'
        +       '<p style="text-align:left">'
        +           'Posted by <span class="author">'+ posthit.user +'</span>'
        +       '</p>'
        +       '<p style="text-align:right">'
        +           '<span>'+ posthit.likes +' <img width="40px" height="40px" src="assets/like.png"/></span>'
        +           '<span>'+ posthit.dislikes+' <a href="#" ><img width="40px" height="40px"  src="assets/dislike.png"/></a></span>'
        +           '<span><a href="#" onclick="World.openSeePostHit('+posthit.id+')"> <img width="30px" height="30px" src="assets/arrow.png" /></a></span>'
        +       '</p>'
        +   '</div>'
        +  '</div>'
        +'</li>';
        $('#BoardPostHitList').append(newDiv);
    },

    formatMessage: function(str){
        return (str.length > 200) ? (str.substring(0, 200) + '...') : (str);
    },

	// request POI data
	requestDataFromServer: function requestDataFromServerFn(lat, lon) {
        if (World.apiService !== null){
            // set helper var to avoid requesting places while loading
            World.isRequestingData = true;

            AR.logger.warning("Before API call");
            AR.logger.warning("BearerToken:"+ World.bearerToken);
            AR.logger.warning("Latitude:"+lat);
            AR.logger.warning("Longitude:"+lon);
            if (World.apiService === null)
                World.apiService = new API(World.bearerToken);
            AR.logger.warning("apiToken:"+ World.apiService.token);
            World.apiService.getBoardsAroundMe(lon, lat, World);
            AR.logger.warning("AFTER API call");
        }
	},

	// helper to sort places by distance
	sortByDistanceSorting: function(a, b) {
		return a.distanceToUser - b.distanceToUser;
	},

	// helper to sort places by distance, descending
	sortByDistanceSortingDescending: function(a, b) {
		return b.distanceToUser - a.distanceToUser;
	},

	addPostHitButtonClicked: function addPostHitButtonClickedFn(board_id) {
		var markerSelectedJSON = {
            action: "present_poi_details",
            id: board_id,
            title: "title",
            description: "description",
            rating: "rating"
        };
	},

	generateTagSelect: function(posthits){
		var tags = [];
		posthits.forEach(function(p){
			p.tags.forEach(function(tag){
				if (!tags.includes(tag)) tags.push(tag);
			});
		});
		var options = '';
		tags.forEach(function(tag){
			options += '<option class="tag" value="'+tag+'">'+ tag +'</option>';
		});
		$('#tagSelect').empty();
		$('#tagSelect').append(options);
		$('#tagSelect').selectmenu('refresh', true);
	},

	loadOnOpenBoard: function(board){

	    World.currentBoard = board;
	    // board informations
        $(".board-name").each(function(){ $(this).html(board.name); });
        $(".board-description").each(function(){ $(this).html(board.description); });
        $(".board-nb-posthits").each(function(){ $(this).html(board.post_hits.length); });
        $("#boardPicture").attr('src', (typeof board.picture !== "undefined") ? (board.picture) : ("assets/default-picture.png"));
	    // generating posthit list
	    $("#BoardPostHitList").empty();
//        var posthits = getMockData("posthit", board.id)
        World.generateTagSelect(board.post_hits);
        board.post_hits.forEach(function(p){  World.generateDivForPosthit(p); });
		$("#panel-test").panel("open", 123);
	},

	onOpenBoard: function(board_id){

	    // API CALL
//	    var board = getMockData("boards")[board_id-1];
		var board = World.apiService.getBoardInfos(board_id, World);

	},

	generateSeePosthit: function(posthit){
		World.currentPostHit = posthit;
		$('#seePostHitTitle').html(posthit.titre);
		$('#seePostHitDescription').html(posthit.message);
		var tags = "";
		 posthit.tags.forEach(function(t){
            tags += '<span class="tag">'+ t +'</span><br/>';
        });
		 $('#seePostHitTags').empty().append(tags);
		 $('#seePostHitAuthor').html(posthit.user);
		 $('#seePostHitLikes').html(posthit.likes);
		 $('#seePostHitDislikes').html(posthit.dislikes);
	},

	openSeePostHit: function(posthitId){
		// affichage du panel posthit
		var posthits = World.currentBoard.post_hits;
		posthits.forEach(function(p){
			if (parseInt(p.id) === posthitId){
				World.generateSeePosthit(p);
			}
		});
		$("#board_content").hide("slide");
		$("#seePostHit").show("slide");
		$("#reportSelct").hide();
		$("#addPosthit").hide();
		$("#buttonAddPosthit").hide();
	},

	backToBoard: function(id){
		// TODO GENERATION DES INFORMATIONS DU POSTHIT + RESET PANEL SEEPOSTHIT
		World.apiService.getBoardInfos(World.currentBoard.id, World);
		World.currentPostHit = null;
		// affichage du panel board => liste posthits
		$("#board_content").show("slide");
		$("#seePostHit").hide("slide");
		$("#addPosthit").hide();
		$("#buttonAddPosthit").show();
	},

	generateReportSelect: function(id){

        $("#reportButton").hide();
        $("#reportFormDiv").show();
	},

	submitReport: function(id){
		$("#reportDiv").hide();
		$.toast({
			 heading: "Submitted",
			 text: "Your report have been submitted. Our moderation team will take it into consideration as soon as possible.",
			 textAlign: 'center',
			 position: 'bottom-center',
		});
	},

	openAddPosthitPanel: function(){
		if ($("#board_content").is(":visible")){ $("#board_content").hide();}
		if ($("#seePostHit").is(":visible")){ $("#seePostHit").hide();}
		$("#addPosthit").show("slide");
		$("#buttonAddPosthit").hide();
	},

	submitAddPosthit: function(){
		var title = ($("#inputTitle").val().length > 0) ? ($("#inputTitle").val()) : (null);
		var message = ($("#inputMessage").val().length > 0) ? ($("#inputMessage").val()) : (null);
		var tokens = ($('#inputTags').val().length > 0) ? (($('#inputTags').val()).trim().split(",")) : (null);
		if (title === null || message === null || tokens === null){
			$.toast({
				 heading: "Error",
				 text: "You must fill the title, message and at least 1 tag to create a posthit!",
				 textAlign: 'center',
				 position: 'bottom-center',
			});
		}
		else{
			World.apiService.postPostHit(title, message, tokens, World);
		}
	},

	validateSubmitAddPosthit: function(){
		$.toast({
			 heading: "Posted!",
			 text: "Your PostHit have been posted!",
			 textAlign: 'center',
			 position: 'bottom-center',
		});
		this.backToBoard();
	},

	validatePostOpinion: function(){
		var val;
        if (opinion_type = "LIKE"){
        	val = $('#seePostHitLikes').html();
            $('#seePostHitLikes').html(parseInt(val) + 1);
        }
        else{
        	val = $('#seePostHitDislikes').html();
            $('#seePostHitDislikes').html(parseInt(val) + 1);
        }
        $.toast({
             heading: opinion_type,
             text: "We noted that this posthit got your attention!",
             textAlign: 'center',
             position: 'bottom-center',
        });

	},

	addDislike: function(){

		World.apiService.postOpinion("DISLIKE", World);
	},

	addLike: function(){
		World.apiService.postOpinion("LIKE", World);
	},

    onFilterChange: function(){
        var filters = $('#tagSelect').val();
        var posthits = World.currentBoard.post_hits;
        $('#BoardPostHitList').empty();
        var display;
        posthits.forEach(function(p){
	        display = (filters === null) ? (true) : (false);
	        if (display === false){
	        	p.tags.forEach(function(t){
	        		if (filters.includes(t) && display === false)
						display = true;
	        	});
	        }
	        if (display === true )
		        World.generateDivForPosthit(p);
        });
    },

	onScreenClick: function(){
	    return;
	},

	loadApiToken: function(json){
        AR.logger.error(json.token);
		World.apiService = new API(json.token);
		World.bearerToken = json.token;
	}
};

$(document).on('swipeleft, swiperight', '#panel-test', function(e){
    return;
});


// token input
$(document).ready(function(){
	$('#inputTags').tokenfield({
		minWidth: 60,
		minLength: 0,
		allowEditing: true,
		allowPasting: true,
		limit: 0,
		autocomplete: {},
		typeahead: {},
		showAutocompleteOnFocus: false,
		createTokensOnBlur: false,
		delimiter: ',',
		beautify: true,
		inputType: 'text'
	});
});
/* get api token */
AR.platform.sendJSONObject({'action':'get_authentication_token'});
/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;
/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;
