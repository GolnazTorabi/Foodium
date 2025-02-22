/*
 * MIT License
 *
 * Copyright (c) 2020 Shreyas Patil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.shreyaspatil.foodium.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.shreyaspatil.foodium.R
import dev.shreyaspatil.foodium.databinding.ActivityPostDetailsBinding
import dev.shreyaspatil.foodium.model.Post
import dev.shreyaspatil.foodium.ui.base.BaseActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PostDetailsActivity : BaseActivity<PostDetailsViewModel, ActivityPostDetailsBinding>() {

    override val mViewModel: PostDetailsViewModel by viewModels()
    private lateinit var binding: ActivityPostDetailsBinding

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_details)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this

        setSupportActionBar(mViewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val postId = intent.extras?.getInt(POST_ID)
            ?: throw IllegalArgumentException("`postId` must be non-null")

        mViewModel.getPost(postId)
        initPost()
    }

    private fun initPost() {
        mViewModel.postDetail.observe(this, Observer { post ->
            Log.d("TAG", "initPost: $post")
            this.post = post
        })
    }

    private fun share() {
        val shareMsg = getString(R.string.share_message, post.title, post.author)

        val intent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText(shareMsg)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }

    override fun getViewBinding(): ActivityPostDetailsBinding =
        ActivityPostDetailsBinding.inflate(layoutInflater)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }

            R.id.action_share -> {
                share()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val POST_ID = "postId"

        fun getStartIntent(
            context: Context,
            postId: Int
        ) = Intent(context, PostDetailsActivity::class.java).apply { putExtra(POST_ID, postId) }
    }
}
