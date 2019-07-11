var mockBoards = [
	{
		"id": "1",
		"name": "Graffiti Skull",
		"altitude": "0",
		"latitude": "48.7632914",
		"longitude": "2.4137477",
		"created_at": "22012019",
		"created_by": "notwak",
		"updated_at": "15042019",
		"updated_by": "notwak",
		"picture": "https://cdn.pixabay.com/photo/2017/05/13/22/18/graffiti-2310818_960_720.jpg",
		"status": "Active",
		"description": "Graffiti de FCZ sur un mur abandonne.",
		"nbPostHits": "2"
	},
	{
		"id": "2",
		"name": "Statue de Rouget de l'Isle",
		"altitude": "0",
		"latitude": "48.7641532",
		"longitude": "2.4056229",
		"created_at": "22012019",
		"created_by": "jeremox",
		"updated_at": "15042019",
		"updated_by": "jeremox",
		"picture": "https://e-monumen.net/wp-content/uploads/57304.jpg",
		"status": "Active",
		"description": "Statue au milieu de la place de Choisy le Roi",
		"nbPostHits": "2"
	},
	{
		"id": "3",
		"name": "Techno Board",
		"altitude": "0",
		"latitude": "48.7632821",
		"longitude": "2.4137200",
		"created_at": "22012019",
		"created_by": "technoboy",
		"updated_at": "15042019",
		"updated_by": "technoboy",
		"picture": "assets/default-picture.png",
		"status": "Inactive",
		"description": "Boumboumboumboumboumboumboumboum",
		"nbPostHits": "0"
	}
];

var mockPosthitsBoard1 = [
	{
		"id": "1",
		"titre":"Boyz Band",
		"message": "Le crew FCZ etait un boyz band des annees 70.",
		"reputation": "4",
		"likes": "11",
		"dislikes": "2",
		"user": "jube",
		"status": "Active",
		"tags": [
			"#marrant",
			"#histoire"
		],
		"liked": "",
		"reported": "",
		"board": "1"
	},
	{
		"id": "2",
		"titre":"Kebab",
		"message": "Il semblerait que le mur original ait ete celui d'un kebab.",
		"reputation": "69",
		"likes": "12",
		"dislikes": "6",
		"user": "notwak",
		"status": "Active",
		"tags": [
			"#kebab",
			"#marrant",
			"#histoire"
		],
		"liked": "",
		"reported": "",
		"board": "1"
	}
];

var mockPosthitsBoard2 = [
	{
		"id": "3",
		"titre":"Marseillaise",
		"message": "Rouget de l'isle est l'auteur de la marseillaise",
		"reputation": "18",
		"likes": "8",
		"dislikes": "4",
		"user": "technoboy",
		"status": "Active",
		"tags": [
			"#histoire",
			"#marseillaise"
		],
		"liked": "",
		"reported": "",
		"board": "2"
	},
	{
		"id": "4",
		"titre":"Vrai nom",
		"message": "Son vrai nom etait Claude Joseph Rouget, dit de l'Isle",
		"reputation": "12",
		"likes": "50",
		"dislikes": "3",
		"user": "jube",
		"status": "Active",
		"tags": [
			"#histoire",
			"#identite"
		],
		"liked": "",
		"reported": "",
		"board": "2"
	}
];

var getMockData =  function(mock, id){
	switch (mock){
		case "boards":
			return mockBoards;
			break;
		case "posthit":
			switch (id){
				case "1":
					return mockPosthitsBoard1;
					break;
				case "2":
					return mockPosthitsBoard2;
					break;
				default:
					return [];
			}
			break;
	}
}