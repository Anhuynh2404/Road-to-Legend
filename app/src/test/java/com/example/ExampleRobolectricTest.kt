package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.data.SaveGameManager
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.model.TransferOffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Road to Legend", appName)
  }

  @Test
  fun testSaveGameManagerCompatibilityWithInternationalFields() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val offerWithIntl = TransferOffer(
      id = "test_intl_save",
      buyingClubId = "buriram_united",
      buyingClubName = "Buriram United",
      buyingClubShortName = "Buriram",
      sellingClubId = "vn_hanoi_fc",
      sellingClubName = "Hà Nội FC",
      transferFee = 250_000,
      offeredWage = 3000,
      offeredSigningBonus = 15_000,
      buyingClubCountry = "Thái Lan",
      isWorkPermitApproved = false,
      workPermitNote = "Hạn ngạch ngoại binh ASEAN cần bổ sung hồ sơ."
    )

    val vLeague1 = DefaultDatabase.initializeLeague("vleague_1")
    val state = CareerState(
      player = Player(id = "p_save", name = "Cầu Thủ Save", primaryPosition = Position.ST),
      currentClub = vLeague1.clubs.first(),
      currentLeague = vLeague1,
      currentContract = Contract(clubId = vLeague1.clubs.first().id, clubName = vLeague1.clubs.first().name, weeklyWage = 500, startSeason = 1, endSeason = 2),
      nextOpponent = vLeague1.clubs.last(),
      incomingOffers = mutableListOf(offerWithIntl)
    )

    // 1. Kiểm tra serialize và deserialize JSON
    val json = SaveGameManager.serializeState(state)
    val loaded = SaveGameManager.deserializeState(json)

    assertNotNull(loaded)
    assertEquals(1, loaded!!.incomingOffers.size)
    val loadedOffer = loaded.incomingOffers.first()
    assertEquals("Thái Lan", loadedOffer.buyingClubCountry)
    assertEquals(false, loadedOffer.isWorkPermitApproved)
    assertEquals("Hạn ngạch ngoại binh ASEAN cần bổ sung hồ sơ.", loadedOffer.workPermitNote)

    // 2. Kiểm tra lưu và tải qua slot game thực tế với Context
    val saveOk = SaveGameManager.saveGame(context, 1, state)
    assertEquals(true, saveOk)
    val loadedFromSlot = SaveGameManager.loadGame(context, 1)
    assertNotNull(loadedFromSlot)
    assertEquals(1, loadedFromSlot!!.incomingOffers.size)
    assertEquals("Thái Lan", loadedFromSlot.incomingOffers.first().buyingClubCountry)
  }
}
