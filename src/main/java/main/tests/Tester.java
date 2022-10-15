package main.tests;

import services.api.PlayerDbApi;

public class Tester {

    static String[] TestUuids = {
            "f84d344f-55f3-4b46-9ca7-f008c67a8b15",
            "0c003c29-8675-4856-914b-9641e4b6bac3",
            "2535422189221140",
            "00000000-0000-0000-0009-000006D1B380",
            "abcdefg!!!",

            "b10dd31a-9014-43c5-9116-de7d3274a74a",
            "7fea8373-3ecd-41ac-8a0b-918a7791a44a",
            "014d849e-6e59-4640-8ffd-5145189f15e7",
            "970fcd59-b469-4f58-b844-fd258526c251",
            "89233f2a-0712-49e7-93cc-de584bcbc8a0",
            "ec7a46c8-716d-4ad7-b404-d6ef82e2f071",
            "6a598743-94f4-412e-b6a2-01636d83dd44",
            "69cb699f-a5de-4b7b-b086-bae9d9a325e6",
            "1014ff27-263f-4f3c-86e8-e8bc770dd463",
            "54639810-37cc-4524-a2c6-e33b01e2584f",
            "707bda94-c01b-4db4-947c-a282fbfcd1a1",
            "90d6025a-dbe6-45bf-bb10-0c818ff10763",
            "9baced8f-e5d5-4190-84d8-1044dc86a275",
            "942a45a5-37fd-443e-ab17-5d66bd69fc5c",
            "1748d2fb-ef76-46df-afa7-b0284e4575f2",
            "ee3dc48f-562e-4b30-8c0a-1f9d990d390e",
            "1d8a38cb-842f-47b6-b98f-75144da094a0",
            "69441837-5d57-4c90-b1e4-cd10fcf40860",
            "bf515488-589c-41c2-9d2d-24e0ab44e86d",
            "4763c727-945d-487b-9dc3-57c7cac5735d",
            "3b32542b-bda2-43ff-9eb1-8473176c12d9",
            "2952f836-76b4-4d2f-87be-a28ff5a986f9",
            "79ec71ce-6e80-46c8-bf27-f23913e7e9e1",
            "8d9367aa-dd74-4ee3-a312-f37664710295",
            "56761935-fb71-4c25-b330-602c5eb43921",
            "3085a245-896d-47e8-ab1c-c5fa26290f38",
            "e77d83e8-16e1-407b-b1db-d38f9ef2e5a3",
            "bf336b9d-a0d1-44e7-81d4-f131c9508a0e",
            "08a648e8-b30b-4418-a8d5-1c28d139bae9",
            "c8d4dd95-f846-4e2b-93a8-4b92e2461fdc",
            "b00dae1e-3255-47ce-bc7a-68d500397218",
            "d663cb25-90cb-4771-ba27-549cefd3bf23",
            "4e33f28d-ef7f-474f-a98a-c2d191404962",
            "a0f0464a-fbcc-4d42-bc2c-fc1b33c3da64",
            "4508df12-4987-46f9-b72e-b098b76609f3",
            "3cd05b04-52c1-4c06-afe6-875a91be22e7",
            "f45736c0-7695-469b-b75d-18cff8d301d1",
            "ae408ff1-6e9e-4cfe-bcfd-e26e6fec3ef1",
            "fbcface6-48bb-4fc7-abb7-cce2f7fdc3ce",
            "c2571904-17b9-4d84-94a2-798ffa2d7e61",
            "0d5408f0-45b7-47d3-a769-2434433a5d28",
            "daacf753-0f56-40b1-b023-382388dab228",
            "0b66678c-6b78-431b-9a72-b8068db540e6",
            "9762d762-41a8-4b9c-8589-272e74928587",
            "7f0e5858-58ca-443f-808a-d5464946268d",
            "b9db50d9-6f47-44be-89a5-658e8b9dfce3",
            "96c16514-ed72-4f65-b415-2655bb740d7e",
            "572fd246-a267-4d11-b4bd-e2b57a05e0a6",
            "9267bb79-baef-4de8-a0eb-6e7e5c96d4cb",
            "7f45c6aa-0470-4a6b-a915-7d27c425b0a1",
            "3fef7f4b-99ee-4412-82f1-1051e24e6f05",
            "9bd72f65-99a3-4cdd-8fdb-14d36c5e1561",
            "f05c38e5-18f1-48d7-9497-1758515319fe",
            "80c45d6f-5639-42d7-b805-ef212b766293",
            "025decea-6c06-4078-863e-3637b459f6a1",
            "b8a484f0-e2fb-4d44-a3b0-4264a5f89407",
            "63767acb-e985-4e9a-9ae7-37a915b8b38e",
            "048c535f-b5fb-407b-afa1-1816bc3a9098"
    };

    static String[] TestPseudo = {
            "Je'suis mal écris",
            "XK12Pomme",
            "12345678900",
            
            "VBH",
            "UkraineFTW",
            "FinishPvP",
            "Kukaa",
            "TriaIs",
            "Assasins",
            "9gy",
            "F95",
            "Deulm",
            "Ethnical",
            "1YE",
            "nny",
            "ken_H1maz1n",
            "JuIia",
            "g3k",
            "kirb",
            "Yefi",
            "jji",
            "tGO",
            "voce",
            "E6Y",
            "xxt",
            "F7I",
            "cesah",
            "y6h",
            "0o8",
            "Zyohk",
            "Disprove",
            "mosts",
            "EddieMunson",
            "Hovus",
            "clickys",
            "9tj",
            "Buzzer",
            "pvpmilk",
            "1BA",
            "Odyssey",
            "Fearer",
            "imissher123",
            "Hollybite",
            "VQ0",
            "ugged",
            "timoket1",
            "ES5",
            "9kk",
            "GjV",
            "Emeu",
            "Reme",
            "clh",
            "Gorra",
            "Empresario",
            "Uwy",
            "yomp",
            "_ot",
            "Sussano",
            "l97",
            "RubiusOMG",
            "nextvictim",
            "bafey",
            "ogareklol"
    };

    static int counter = 0;

    public static void TestUUIDLookup() {
        counter = 0;
        for (String uuid : TestUuids) {

            PlayerDbApi.fetchInfosWithUuid(uuid);
            counter++;
        }
    }

    public static void TestPseudoLookup() {
        counter = 0;
        for (String pseudo : TestPseudo) {

            PlayerDbApi.fetchInfosWithPseudo(pseudo);
            counter++;
        }
    }
}
