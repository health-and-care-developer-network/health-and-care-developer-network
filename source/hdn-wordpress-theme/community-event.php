 <?php
/*
Template Name: Community Event Post
*/
?>

<?php get_header(); ?>
		
<div role="main" class="main">
	<div class="page_title">
		<div class="wrapper">
			<hgroup>
				<h1>Community</h1>
				<h2>Read blogs, news and events; participate in forums</h2>
			</hgroup>
		</div>
	</div><!--end page title-->
	<div class="main_content cf">
		<?php include_once "breadcrumbs.php"; ?>

		<div class="wrapper cf container">
			<div class="one_quarter tree_wrap">
				<div class="left_bar_list">
					<h3>Categories</h3>
					<ul>
					<?php wp_list_categories('taxonomy=community-categories&orderby=name&show_count=1&title_li='); ?>
				</div><!--end left bar list-->
				
				<div class="left_bar_list">
					<h3>Key Words</h3>
					<ul>
						<?php wp_list_categories('taxonomy=community-key-words&orderby=name&show_count=1&title_li='); ?>
					</ul>
				</div><!--end left bar list-->
			</div>

			<div class="three_quarters">
				<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
					<div class="content_wrap cf">
						<article class="cf">
							<div class="three_quarters art_txt_area">
								<hgroup>
									<h2><?php the_title(); ?></h2>
									<h3 class="delta"><?php the_field('sub_title'); ?></h3>
									<ul class="blog_meta cf">
										<li class="by"><span class="meta_icon" aria-hidden="true" data-icon="&#x23;"></span><?php the_author(); ?> </li>
										<li class="on"><span class="meta_icon" aria-hidden="true" data-icon="&#x25;"></span><?php the_time('j M Y'); ?></li>
										<li class="in"><span class="meta_icon" aria-hidden="true" data-icon="&#x2c;"></span><?php the_terms( $post->ID, 'community-categories', '', '', '' ); ?></li>
										<li class="tags"><span class="meta_icon" aria-hidden="true" data-icon="&#x2e;"></span><?php the_terms( $post->ID, 'community-key-words', '', ', ', '' ); ?></li>
									</ul>
								</hgroup>
								

								<div class="art_txt">
								<?php the_content(); ?>
								</div>

								<?php include_once "likes.php"; ?>
							</div><!--end three quarters-->
							
							<aside class="one_quarter link_lists">
								<?php if( get_field('link_list') ): ?>
								<?php while( has_sub_field('link_list') ): ?>
									<div class="list">
										<ul class="attachments <?php the_sub_field('ll_title'); ?>">
											<li>
												<h4><?php the_sub_field('ll_title'); ?></h4>
											</li>
											<?php if( get_sub_field('link') ): ?>
											<?php while( has_sub_field('link') ): ?>
												<li>
													<span class="att_icon" aria-hidden="true" data-icon="<?php the_sub_field('icon_font'); ?>"></span>
													<a href="<?php the_sub_field('link_url'); ?>" target="<?php the_sub_field('int_or_ext'); ?>"><?php the_sub_field('link_text'); ?></a>
												</li>
											<?php endwhile; ?>
											<?php endif; ?>
										</ul>
									</div><!--end list-->
								<?php endwhile; ?>
								<?php endif; ?>

								<?php include_once "share_tools.php"; ?>
							</aside><!--end one quarter-->

						</article>
					</div><!--end content wrap-->
				<?php endwhile; endif; ?>
			</div><!--end two thirds-->
		</div><!--end wrapper-->
	</div><!--end main_content-->
</div><!-- end main -->
		
<?php get_footer(); ?>
