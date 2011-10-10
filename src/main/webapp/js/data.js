var data = {
	"_class" : "com.github.dansmithy.sanjuan.model.Game",
	"_id" : ObjectId("4e931501e4b050f5365f4d58"),
	"currentTariff" : 0,
	"deck" : {
		"_class" : "com.github.dansmithy.sanjuan.model.Deck",
		"supply" : [ 62, 58, 8, 61, 102, 28, 21, 89, 84, 65, 44, 3, 103, 99,
				98, 25, 48, 45, 88, 11, 74, 26, 92, 60, 20, 39, 19, 66, 76, 52,
				82, 16, 17, 79, 36, 69, 106, 31, 50, 104, 54, 15, 70, 42, 10,
				23, 94, 85, 4, 105, 34, 87, 68, 35, 7, 71, 9, 40, 6, 37, 64,
				30, 2, 55, 27, 13, 51, 24, 101, 32, 33, 83, 56, 1, 86, 49, 91,
				43, 63, 57, 22, 72, 78, 77, 107, 73, 93, 47, 41, 90, 29, 53,
				80, 97, 81, 5 ],
		"discard" : []
	},
	"gameId" : NumberLong(25),
	"owner" : "danny",
	"players" : [ {
		"_class" : "com.github.dansmithy.sanjuan.model.Player",
		"hand" : [ 38, 59, 108, 18, 95 ],
		"chapelCards" : [],
		"name" : "danny",
		"goods" : {
			"110" : 12
		},
		"buildings" : [ 110 ]
	}, {
		"_class" : "com.github.dansmithy.sanjuan.model.Player",
		"hand" : [ 100, 67, 96, 14, 75 ],
		"chapelCards" : [],
		"name" : "isabelle",
		"goods" : {
			"109" : 46
		},
		"buildings" : [ 109 ]
	} ],
	"rounds" : [ {
		"governor" : "danny",
		"phases" : [ {
			"_class" : "com.github.dansmithy.sanjuan.model.Phase",
			"leadPlayer" : "danny",
			"playerCount" : 2,
			"plays" : [ {
				"_class" : "com.github.dansmithy.sanjuan.model.Play",
				"offered" : {
					"goodsCanProduce" : 2,
					"factoriesCanProduce" : [ 110 ]
				},
				"hasPrivilige" : true,
				"player" : "danny",
				"state" : "COMPLETED",
				"playChoice" : {
					"payment" : [],
					"productionFactories" : [ 110 ]
				}
			}, {
				"_class" : "com.github.dansmithy.sanjuan.model.Play",
				"offered" : {
					"goodsCanProduce" : 1,
					"factoriesCanProduce" : [ 109 ]
				},
				"hasPrivilige" : false,
				"player" : "isabelle",
				"state" : "COMPLETED",
				"playChoice" : {
					"payment" : [],
					"productionFactories" : [ 109 ]
				}
			} ],
			"role" : "PRODUCER"
		}, {
			"_class" : "com.github.dansmithy.sanjuan.model.Phase",
			"plays" : [],
			"playerCount" : 2,
			"leadPlayer" : "isabelle"
		} ],
		"playerCount" : 2
	} ],
	"state" : "PLAYING",
	"tariffs" : [ {
		"prices" : [ 1, 1, 1, 2, 2 ]
	}, {
		"prices" : [ 1, 2, 2, 3, 3 ]
	}, {
		"prices" : [ 1, 1, 2, 2, 3 ]
	}, {
		"prices" : [ 1, 2, 2, 2, 3 ]
	}, {
		"prices" : [ 1, 1, 2, 2, 2 ]
	} ],
	"version" : NumberLong(6)
}
